package york.pharmacy.orders;

import york.pharmacy.medicines.Medicine;
import york.pharmacy.orders.dto.OrderRequest;
import york.pharmacy.orders.dto.OrderResponse;

public class OrderMapper {

    // Convert OrderRequest DTO to Order Entity
    public static Order toEntity(OrderRequest orderRequest, Medicine medicine) {
        Order order = new Order();
        order.setMedicine(medicine);
        order.setQuantity(orderRequest.getQuantity());
        order.setDeliveryDate(orderRequest.getDeliveryDate());
        order.setStatus(OrderStatus.ORDERED); // Default status for new orders
        return order;
    }

    // Convert Order Entity to OrderResponse DTO
    public static OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setMedicine(order.getMedicine());
        response.setQuantity(order.getQuantity());
        response.setDeliveryDate(order.getDeliveryDate());
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        return response;
    }
}
