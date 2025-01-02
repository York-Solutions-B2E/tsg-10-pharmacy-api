
package york.pharmacy.inventory;

import york.pharmacy.inventory.dto.InventoryRequest;
import york.pharmacy.inventory.dto.InventoryResponse;

public class InventoryMapper {
    public static Inventory toEntity(InventoryRequest request) {
        return Inventory.builder()
                .medicineId(request.getMedicineId())
                .stockQuantity(request.getStockQuantity())
                .build();
    }

    public static InventoryResponse toResponse(Inventory entity) {
        return InventoryResponse.builder()
                .id(entity.getId())
                .medicineId(entity.getMedicineId())
                .stockQuantity(entity.getStockQuantity())
                .build();
    }
}