package york.pharmacy.medInventory;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Entity representing medicine inventory data.
 * Demonstrates Lombok and JPA/Jakarta annotations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "med_inventory")
public class MedInventoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "med_name", nullable = false)
    private String medName;

    /**
     * NOTE: The actual CHECK(stock_count >= 0) constraint
     * should be enforced at the DB-level (in your schema DDL).
     * For Bean Validation, you could add something like
     * @Min(0) if you want to fail fast in your Java code.
     */
    @Column(name = "stock_count", nullable = false)
    private Integer stockCount;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;
}
