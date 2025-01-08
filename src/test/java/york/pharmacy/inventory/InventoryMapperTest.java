package york.pharmacy.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import york.pharmacy.inventory.dto.InventoryRequest;
import york.pharmacy.inventory.dto.InventoryResponse;
import york.pharmacy.medicines.Medicine;

import java.time.Instant;


import static org.junit.jupiter.api.Assertions.*;

class InventoryMapperTest {
    private Medicine medicine;

    @BeforeEach
    void setUp() {
        medicine = new Medicine(2L, "Jelly Beans", "J-01", Instant.now(), Instant.now());
    }


    @Test
    @DisplayName("Should map InventoryRequest to Inventory entity correctly")
    void testToEntity() {
        InventoryRequest request = InventoryRequest.builder()
                .medicineId(1L)
                .stockQuantity(100)
                .build();

        Inventory entity = InventoryMapper.toEntity(request,medicine);

        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals(2L, entity.getMedicine().getId());
        assertEquals(100, entity.getStockQuantity());
    }

    @Test
    @DisplayName("Should map InventoryRequest to Inventory entity with default sufficientStock")
    void testToEntityWithDefaultSufficientStock() {
        InventoryRequest request = InventoryRequest.builder()
                .medicineId(1L)
                .stockQuantity(100)
                .build();

        Inventory entity = InventoryMapper.toEntity(request, medicine);

        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals(2L, entity.getMedicine().getId());
        assertEquals(100, entity.getStockQuantity());
    }

    @Test
    @DisplayName("Should map Inventory entity to InventoryResponse correctly")
    void testToResponse() {
        Inventory entity = Inventory.builder()
                .id(1L)
                .medicine(medicine)
                .stockQuantity(50)
                .build();

        InventoryResponse response = InventoryMapper.toResponse(entity);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(2L, response.getMedicine().getId());
        assertEquals(50, response.getStockQuantity());
    }

    @Test
    @DisplayName("Should map Inventory entity to InventoryResponse with default sufficientStock")
    void testToResponseWithDefaultSufficientStock() {
        Inventory entity = Inventory.builder()
                .id(1L)
                .medicine(medicine)
                .stockQuantity(50)
                .build();

        InventoryResponse response = InventoryMapper.toResponse(entity);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(2L, response.getMedicine().getId());
        assertEquals(50, response.getStockQuantity());
    }
}