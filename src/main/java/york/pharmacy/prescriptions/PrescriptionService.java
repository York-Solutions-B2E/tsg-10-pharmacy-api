package york.pharmacy.prescriptions;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import york.pharmacy.exceptions.ResourceNotFoundException;
import york.pharmacy.inventory.InventoryService;
import york.pharmacy.medicines.Medicine;
import york.pharmacy.medicines.MedicineService;
import york.pharmacy.orders.Order;
import york.pharmacy.prescriptions.dto.PrescriptionRequest;
import york.pharmacy.prescriptions.dto.PrescriptionResponse;
import york.pharmacy.prescriptions.dto.PrescriptionStatusRequest;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final MedicineService medicineService;
    private final InventoryService inventoryService;

    // create a new prescription
    public PrescriptionResponse addPrescription(PrescriptionRequest prescriptionRequest) {
        Medicine medicine = medicineService.getMedicineByCode(prescriptionRequest.getMedicineCode()); // need to add this method
        Prescription prescription = PrescriptionMapper.toEntity(prescriptionRequest, medicine);
        Prescription savedPrescription = prescriptionRepository.save(prescription);
        updateInventoryStockStatus(medicine.getId());
        // Kafka publish RECEIVED

        return PrescriptionMapper.toResponse(savedPrescription);
    }

    // get all prescriptions
    public List<PrescriptionResponse> getAllPrescriptions() {
        return prescriptionRepository.findAll()
                .stream()
                .map(PrescriptionMapper::toResponse)
                .collect(Collectors.toList());
    }

    // get all active prescriptions
    public List<PrescriptionResponse> getActivePrescriptions() {
        return prescriptionRepository.findAllByStatusExcept(
                List.of(PrescriptionStatus.CANCELLED, PrescriptionStatus.PICKED_UP))
                .stream()
                .map(PrescriptionMapper::toResponse)
                .collect(Collectors.toList());
    }

    // get prescription by ID
    public PrescriptionResponse getPrescriptionById(Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription with id" + id + " not found")  );
        return PrescriptionMapper.toResponse(prescription);
    }

    // update a prescription
    // this needs to be modified.....
    public PrescriptionResponse updatePrescription(Long id, PrescriptionStatusRequest prescriptionStatusRequest) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription with id" + id + " not found") );
        PrescriptionStatus status = prescriptionStatusRequest.getStatus();
        if (status == PrescriptionStatus.FILLED) {
            if (prescription.getStatus() == PrescriptionStatus.NEW || prescription.getStatus() == PrescriptionStatus.STOCK_RECEIVED) {
                // notify Inventory to update stock
                Long medicineId = prescription.getMedicine().getId();
                int negativeCount = -prescription.getQuantity();
                inventoryService.adjustStockQuantity(medicineId, negativeCount);
                prescription.setStatus(status);

                // kafka publish FILLED event
            } else {
                throw new IllegalStateException("Prescription cannot be marked as FILLED from the current state: " + prescription.getStatus());
            }


        } else if (status == PrescriptionStatus.PICKED_UP) {
            if (prescription.getStatus() == PrescriptionStatus.FILLED) {
                // kafka publish PICKED_UP
                prescription.setStatus(status);
            } else {
                throw new IllegalStateException("Prescription cannot be marked as PICKED_UP from the current state: " + prescription.getStatus());
            }

        }

        Prescription updatedPrescription = prescriptionRepository.save(prescription);
        return PrescriptionMapper.toResponse(updatedPrescription);
    }

    public List<Prescription> updateAwaitingShipmentStatus(Order order) {
        List<PrescriptionStatus> statuses = List.of(PrescriptionStatus.NEW, PrescriptionStatus.OUT_OF_STOCK);
        List<Prescription> prescriptions = prescriptionRepository.findAllByMedicineIdAndStatus(order.getInventory().getMedicine().getId(), statuses);
        for (Prescription p : prescriptions) {
            p.setOrder(order);
            p.setStatus(PrescriptionStatus.AWAITING_SHIPMENT);
            //publish to kafka BACK_ORDERED, p.getPrescriptionNumber(), order.getDate()
            Prescription savedPrescription = prescriptionRepository.save(p);
        }

        return prescriptions;
    }

    public List<Prescription> updateStockReceivedStatus(Order order) {
        List<Prescription> prescriptions = prescriptionRepository.findAllByOrder(order);

        for (Prescription p : prescriptions) {
            p.setStatus(PrescriptionStatus.STOCK_RECEIVED);
            Prescription savedPrescription = prescriptionRepository.save(p);
        }
        updateInventoryStockStatus(order.getMedicine().getId());

        return prescriptions;
    }

    // cancel prescription
    public void cancelPrescription(Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription with id" + id + " not found") );

        prescription.setStatus(PrescriptionStatus.CANCELLED);
        prescriptionRepository.save(prescription);
    }

    // need a helper function to return the needed count for new orders
    public int minOrderCount(Long medicineId) {
        List<PrescriptionStatus> statuses = List.of(PrescriptionStatus.NEW, PrescriptionStatus.OUT_OF_STOCK);

        return prescriptionRepository.findTotalQuantityByMedicineIdAndStatus(medicineId, statuses);
    }

    // send med id, total count of active prescriptions for that medicine
    // include STOCK_RECEIVED
    public void updateInventoryStockStatus(Long medicineId) {
        List<PrescriptionStatus> statuses = List.of(PrescriptionStatus.NEW, PrescriptionStatus.OUT_OF_STOCK, PrescriptionStatus.STOCK_RECEIVED);
        HashMap<Long, Integer> medicineCount = new HashMap<>();
        int totalCount = prescriptionRepository.findTotalQuantityByMedicineIdAndStatus(medicineId, statuses);

        medicineCount.put(medicineId, totalCount);
        inventoryService.updateSufficientStock(medicineCount);
    }


}
