package york.pharmacy.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDeliveryDateResponse {
    private long orderId;
    private LocalDate deliveryDate;
}
