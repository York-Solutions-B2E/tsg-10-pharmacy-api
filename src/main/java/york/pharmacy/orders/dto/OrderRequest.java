package york.pharmacy.orders.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import york.pharmacy.orders.OrderStatus;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    @NotNull(message = "Medicine ID cannot be null")
    private Long medicineId;

    @NotNull
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    @NotNull(message = "Delivery date cannot be null")
    @Future(message = "Delivery date must be in the future")
    private LocalDate deliveryDate;

    private OrderStatus status;

    public OrderRequest(Long medId, int quantity, LocalDate date) {
        this.medicineId = medId;
        this.quantity = quantity;
        this.deliveryDate = date;
    }
}
