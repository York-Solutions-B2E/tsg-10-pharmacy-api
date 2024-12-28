package york.pharmacy.medInventory.dto;

import lombok.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * DTO for creating or updating MedInventory records.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedInventoryRequest {

    @NotNull(message = "Medicine name cannot be null")
    private String medName;

    /**
     * If you want to ensure stockCount >= 0 in the request,
     * you can add a validation annotation:
     */
    @NotNull(message = "Stock count is required")
    @Min(value = 0, message = "Stock count cannot be negative")
    private Integer stockCount;

    // Optional field, so we won't use @NotNull here
    private LocalDate deliveryDate;
}
