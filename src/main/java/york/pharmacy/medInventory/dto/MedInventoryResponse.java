package york.pharmacy.medInventory.dto;

import lombok.*;

import java.time.LocalDate;

/**
 * DTO for returning MedInventory data to the client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedInventoryResponse {

    private Long id;
    private String medName;
    private Integer stockCount;
    private LocalDate deliveryDate;
}
