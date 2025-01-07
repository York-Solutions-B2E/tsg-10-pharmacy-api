package york.pharmacy.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import york.pharmacy.medicines.Medicine;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {
    private Long id;
    private Medicine medicine;
    private Integer stockQuantity;
    private Boolean sufficientStock;
    private LocalDate deliveryDate;
    private Integer minimumOrderCount;
}