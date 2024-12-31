package york.pharmacy.medicines.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicineResponse {
    private long id;
    private String name;
    private String code;
    private Instant createdAt;
    private Instant updatedAt;
}

