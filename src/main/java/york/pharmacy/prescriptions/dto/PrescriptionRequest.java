package york.pharmacy.prescriptions.dto;

import jakarta.annotation.Nullable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import york.pharmacy.prescriptions.PrescriptionStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionRequest {

    @NotNull
    private Long patientId;

    @NotNull
    private Long medicineId;

    @NotNull
    private Long prescriptionNumber;

    @NotNull
    private int quantity;

    @NotNull
    private String description;

//    @Nullable
//    @Enumerated(EnumType.STRING)
//    private PrescriptionStatus status;

}
