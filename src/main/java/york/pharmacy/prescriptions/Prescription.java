package york.pharmacy.prescriptions;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long patientId;

    @NotNull
    private Long medicineId;

    @NotNull
    private Long prescriptionNumber;

    @NotNull
    private int quantity;

    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PrescriptionStatus status;
}
