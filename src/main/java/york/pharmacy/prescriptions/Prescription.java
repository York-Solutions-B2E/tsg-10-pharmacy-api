package york.pharmacy.prescriptions;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import york.pharmacy.medicines.Medicine;
import york.pharmacy.orders.Order;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(updatable = false)
    private String patientId;


    @ManyToOne
    @JoinColumn(name = "medicine_id")
    private Medicine medicine;

    @NotNull
    @Column(updatable = false, unique = true)
    private String prescriptionNumber;

    @NotNull
    @Column(updatable = false)
    private int quantity;

    private String instructions;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PrescriptionStatus status;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = true)
    private Order order;
}
