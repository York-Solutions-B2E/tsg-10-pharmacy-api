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
                .sufficientStock(true)
                .build();

        Inventory entity = InventoryMapper.toEntity(request);

        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals(1L, entity.getMedicineId());
        assertEquals(100, entity.getStockQuantity());
        assertTrue(entity.getSufficientStock());
    }

    @Test
    @DisplayName("Should map InventoryRequest to Inventory entity with default sufficientStock")
    void testToEntityWithDefaultSufficientStock() {
        InventoryRequest request = InventoryRequest.builder()
                .medicineId(1L)
                .stockQuantity(100)
                .build();

        Inventory entity = InventoryMapper.toEntity(request);

        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals(1L, entity.getMedicineId());
        assertEquals(100, entity.getStockQuantity());
        assertNull(entity.getSufficientStock()); // Default value should be null
    }

    @Test
    @DisplayName("Should map Inventory entity to InventoryResponse correctly")
    void testToResponse() {
        Inventory entity = Inventory.builder()
                .id(1L)
                .medicineId(2L)
                .stockQuantity(50)
                .sufficientStock(true)
                .build();

        InventoryResponse response = InventoryMapper.toResponse(entity);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(2L, response.getMedicineId());
        assertEquals(50, response.getStockQuantity());
        assertTrue(response.getSufficientStock());
    }

    @Test
    @DisplayName("Should map Inventory entity to InventoryResponse with default sufficientStock")
    void testToResponseWithDefaultSufficientStock() {
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
        assertNull(response.getSufficientStock()); // Default value should be null
    }
}