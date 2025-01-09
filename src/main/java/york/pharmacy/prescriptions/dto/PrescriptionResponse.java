package york.pharmacy.prescriptions.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import york.pharmacy.medicines.Medicine;
import york.pharmacy.prescriptions.PrescriptionStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionResponse {

    private Long id;
    private String patientId;
    private Medicine medicine;
    private String prescriptionNumber;
    private int quantity;
    private String instructions;
    private PrescriptionStatus status;
}
