package york.pharmacy.prescriptions.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import york.pharmacy.prescriptions.PrescriptionStatus;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class PrescriptionStatusRequest {
    @NotNull
    @Enumerated(EnumType.STRING)
    private PrescriptionStatus status;
}
