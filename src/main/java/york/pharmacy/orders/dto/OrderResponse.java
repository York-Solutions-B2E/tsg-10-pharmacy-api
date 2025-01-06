package york.pharmacy.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import york.pharmacy.inventory.Inventory;
import york.pharmacy.medicines.Medicine;
import york.pharmacy.orders.OrderStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private long id;
    private Inventory inventory;
    private int quantity;
    private LocalDate deliveryDate;
    private OrderStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
