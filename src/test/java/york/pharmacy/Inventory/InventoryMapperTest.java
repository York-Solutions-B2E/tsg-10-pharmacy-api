package york.pharmacy.inventory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import york.pharmacy.inventory.dto.InventoryRequest;
import york.pharmacy.inventory.dto.InventoryResponse;

import static org.junit.jupiter.api.Assertions.*;

class InventoryMapperTest {

    @Test
    @DisplayName("Should map InventoryRequest to Inventory entity correctly")
    void testToEntity() {
        InventoryRequest request = InventoryRequest.builder()
                .medicineId(1L)
                .stockQuantity(100)
                .build();

        Inventory entity = InventoryMapper.toEntity(request);

        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals(1L, entity.getMedicineId());
        assertEquals(100, entity.getStockQuantity());
    }

    @Test
    @DisplayName("Should map Inventory entity to InventoryResponse correctly")
    void testToResponse() {
        Inventory entity = Inventory.builder()
                .id(1L)
                .medicineId(2L)
                .stockQuantity(50)
                .build();

        InventoryResponse response = InventoryMapper.toResponse(entity);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(2L, response.getMedicineId());
        assertEquals(50, response.getStockQuantity());
    }
}