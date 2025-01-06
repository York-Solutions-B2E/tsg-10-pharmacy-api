package york.pharmacy.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequest {
    @NotNull(message = "Medicine ID is required")
    private Long medicineId;

    @Min(value = 0, message = "Stock quantity must be non-negative")
    @NotNull(message = "Stock quantity is required")
    private Integer stockQuantity;

}