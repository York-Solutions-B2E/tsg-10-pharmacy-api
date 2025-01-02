package york.pharmacy.prescriptions;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import york.pharmacy.medicines.Medicine;

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

//    @NotNull
//    private Long medicineId;

    @ManyToOne
    @JoinColumn(name = "medicine_id")
    private Medicine medicine;

    @NotNull
    private Long prescriptionNumber;

    @NotNull
    private int quantity;

    private String instructions;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PrescriptionStatus status;
}
