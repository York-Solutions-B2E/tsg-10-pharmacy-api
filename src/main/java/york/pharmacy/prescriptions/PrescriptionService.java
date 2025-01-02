package york.pharmacy.prescriptions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import york.pharmacy.exceptions.ResourceNotFoundException;
import york.pharmacy.inventory.InventoryService;
import york.pharmacy.medicines.Medicine;
import york.pharmacy.medicines.MedicineService;
import york.pharmacy.prescriptions.dto.PrescriptionRequest;
import york.pharmacy.prescriptions.dto.PrescriptionResponse;
import york.pharmacy.prescriptions.dto.PrescriptionStatusRequest;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
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

    // get prescription by ID
    public PrescriptionResponse getPrescriptionById(Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription with id" + id + " not found")  );
        return PrescriptionMapper.toResponse(prescription);
    }

    // update a prescription
    public PrescriptionResponse updatePrescription(Long id, PrescriptionStatusRequest prescriptionStatusRequest) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription with id" + id + " not found") );
        PrescriptionStatus status = prescriptionStatusRequest.getStatus();
        if (status == PrescriptionStatus.FILLED) {
            // notify Inventory to update stock
            /**
             * Long medicineId = prescription.getMedicine().getId();
             * inventoryService.decreaseStock(medicineId, prescription.getQuantity())
             * */
            // kafka publich FILLED event

        } else if (status == PrescriptionStatus.AWAITING_SHIPMENT) {
            // kafka publish BACK_ORDERED
            // include the date
        } else if (status == PrescriptionStatus.PICKED_UP) {
            // kafka publish PICKED_UP
        }

        prescription.setStatus(prescriptionStatusRequest.getStatus());
        Prescription updatedPrescription = prescriptionRepository.save(prescription);
        return PrescriptionMapper.toResponse(updatedPrescription);
    }


    // cancel prescription

    //    call updatePrescription with cancelled
    public void cancelPrescription(Long id) {
//        Prescription prescription = prescriptionRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Prescription with id" + id + " not found") );

        PrescriptionStatusRequest statusRequest = new PrescriptionStatusRequest(PrescriptionStatus.CANCELLED);
        updatePrescription(id, statusRequest);
    }

    // send med id, total count of active prescriptions for that medicine
    public void updateInventoryStockStatus(Long medicineId) {
        HashMap<Long, Integer> medicineCount = new HashMap<>();
        int totalCount = prescriptionRepository.findTotalQuantityByMedicineIdAndStatus(medicineId);

        medicineCount.put(medicineId, totalCount);
        inventoryService.updateSufficientStock(medicineCount);
    }
}
