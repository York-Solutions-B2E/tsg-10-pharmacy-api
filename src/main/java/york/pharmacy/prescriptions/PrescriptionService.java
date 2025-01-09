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
import york.pharmacy.utilities.ServiceUtility;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final ServiceUtility serviceUtility;

    // create a new prescription
    public PrescriptionResponse addPrescription(PrescriptionRequest prescriptionRequest) {
        Medicine medicine = serviceUtility.getMedicineByCode(prescriptionRequest.getMedicineCode()); // need to add this method
        Prescription prescription = PrescriptionMapper.toEntity(prescriptionRequest, medicine);

        Long medicineId = medicine.getId();
        Long prescriptionId = prescription.getId();
        serviceUtility.checkAndUpdatePrescriptionStock(medicineId, prescriptionId);

        Prescription savedPrescription = prescriptionRepository.save(prescription);
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
                serviceUtility.adjustStockQuantity(medicineId, negativeCount);
                prescription.setStatus(status);

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

}
