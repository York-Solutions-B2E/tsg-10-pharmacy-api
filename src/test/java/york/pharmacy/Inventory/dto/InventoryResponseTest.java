package york.pharmacy.Inventory.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InventoryResponseTest {

    @Test
    @DisplayName("Test builder, getters, and setters for InventoryResponse")
    void testInventoryResponse() {
        InventoryResponse response = InventoryResponse.builder()
                .id(1L)
                .medicineId(2L)
                .stockQuantity(15)
                .build();

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(2L, response.getMedicineId());
        assertEquals(15, response.getStockQuantity());

        response.setStockQuantity(20);
        assertEquals(20, response.getStockQuantity());
    }
}