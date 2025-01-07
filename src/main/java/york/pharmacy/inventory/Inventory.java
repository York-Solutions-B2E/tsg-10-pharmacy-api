
package york.pharmacy.inventory;

import jakarta.persistence.*;
import lombok.*;
import york.pharmacy.medicines.Medicine;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inventory",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"medicine_id"})
        })
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Builder.Default
    @Column(name = "sufficient_stock")
    private Boolean sufficientStock = Boolean.TRUE;
}