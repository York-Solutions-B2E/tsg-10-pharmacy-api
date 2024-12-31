package york.pharmacy.medicines.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicineRequest {
    @NotNull(message = "Name cannot be null")
    private String name;

    @NotNull(message = "Medicine Code cannot be null")
    private String code;
}