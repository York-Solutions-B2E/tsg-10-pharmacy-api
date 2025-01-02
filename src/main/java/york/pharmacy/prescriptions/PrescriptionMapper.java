package york.pharmacy.prescriptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import york.pharmacy.medicines.Medicine;
import york.pharmacy.prescriptions.dto.PrescriptionRequest;
import york.pharmacy.prescriptions.dto.PrescriptionResponse;


public class PrescriptionMapper {

    public static Prescription toEntity(PrescriptionRequest prescriptionRequest, Medicine medicine) {
        Prescription prescription = new Prescription();
        prescription.setPrescriptionNumber(prescriptionRequest.getPrescriptionNumber());
        prescription.setPatientId(prescriptionRequest.getPatientId());
        prescription.setMedicine(medicine);
        prescription.setQuantity(prescriptionRequest.getQuantity());
        prescription.setInstructions(prescriptionRequest.getInstructions());
        prescription.setStatus(PrescriptionStatus.NEW);

        return prescription;
    }

    public static PrescriptionResponse toResponse(Prescription prescription) {
        PrescriptionResponse prescriptionResponse = new PrescriptionResponse();
        prescriptionResponse.setId(prescription.getId());
        prescriptionResponse.setPrescriptionNumber(prescription.getPrescriptionNumber());
        prescriptionResponse.setPatientId(prescription.getPatientId());
        prescriptionResponse.setMedicine(prescription.getMedicine());
        prescriptionResponse.setQuantity(prescription.getQuantity());
        prescriptionResponse.setInstructions(prescription.getInstructions());
        prescriptionResponse.setStatus(prescription.getStatus());

        return prescriptionResponse;
    }
}
