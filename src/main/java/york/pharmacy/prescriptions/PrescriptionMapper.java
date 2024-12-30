package york.pharmacy.prescriptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import york.pharmacy.prescriptions.dto.PrescriptionRequest;
import york.pharmacy.prescriptions.dto.PrescriptionResponse;


public class PrescriptionMapper {

    public static Prescription toEntity(PrescriptionRequest prescriptionRequest) {
        Prescription prescription = new Prescription();
        prescription.setPrescriptionNumber(prescriptionRequest.getPrescriptionNumber());
        prescription.setPatientId(prescriptionRequest.getPatientId());
        prescription.setMedicineId(prescriptionRequest.getMedicineId());
        prescription.setQuantity(prescriptionRequest.getQuantity());
        prescription.setDescription(prescriptionRequest.getDescription());
        prescription.setStatus(PrescriptionStatus.NEW);

        return prescription;
    }

    public static PrescriptionResponse toResponse(Prescription prescription) {
        PrescriptionResponse prescriptionResponse = new PrescriptionResponse();
        prescriptionResponse.setPrescriptionNumber(prescription.getPrescriptionNumber());
        prescriptionResponse.setPatientId(prescription.getPatientId());
        prescriptionResponse.setMedicineId(prescription.getMedicineId());
        prescriptionResponse.setQuantity(prescription.getQuantity());
        prescriptionResponse.setDescription(prescription.getDescription());
        prescriptionResponse.setStatus(prescription.getStatus());

        return prescriptionResponse;
    }
}
