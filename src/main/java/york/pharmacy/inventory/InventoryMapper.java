package york.pharmacy.inventory;

import york.pharmacy.inventory.dto.InventoryRequest;
import york.pharmacy.inventory.dto.InventoryResponse;
import york.pharmacy.medicines.Medicine;
import york.pharmacy.orders.Order;

import java.util.Optional;

public class InventoryMapper {
    public static Inventory toEntity(InventoryRequest request, Medicine medicine) {
        return Inventory.builder()
                .medicine(medicine)
                .stockQuantity(request.getStockQuantity())
                .sufficientStock(request.getSufficientStock())
                .build();
    }

    // Response mapping with closest order
    public static InventoryResponse toResponse(Inventory entity, Optional<Order> closestOrder) {
        return InventoryResponse.builder()
                .id(entity.getId())
                .medicine(entity.getMedicine())
                .stockQuantity(entity.getStockQuantity())
                .deliveryDate(closestOrder.map(Order::getDeliveryDate).orElse(null))
                .sufficientStock(entity.getSufficientStock())
                .build();
    }

    // Response mapping without closest order
    public static InventoryResponse toResponse(Inventory entity) {
        return InventoryResponse.builder()
                .id(entity.getId())
                .medicine(entity.getMedicine())
                .stockQuantity(entity.getStockQuantity())
                .sufficientStock(entity.getSufficientStock())
                .build();
    }
}