package york.pharmacy.prescriptions.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import york.pharmacy.prescriptions.PrescriptionStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionResponse {

    private Long patientId;
    private Long medicineId;
    private Long prescriptionNumber;
    private int quantity;
    private String description;
    private PrescriptionStatus status;
}
