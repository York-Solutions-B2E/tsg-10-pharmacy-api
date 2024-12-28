package york.pharmacy.medInventory.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MedInventoryResponseTest {

    @Test
    @DisplayName("Test builder, getters, and setters for MedInventoryResponse")
    void testMedInventoryResponse() {
        LocalDate date = LocalDate.of(2024, 12, 31);

        MedInventoryResponse response = MedInventoryResponse.builder()
                .id(1L)
                .medName("Tylenol")
                .stockCount(15)
                .deliveryDate(date)
                .build();

        assertNotNull(response, "Response object should not be null");
        assertEquals(1L, response.getId());
        assertEquals("Tylenol", response.getMedName());
        assertEquals(15, response.getStockCount());
        assertEquals(date, response.getDeliveryDate());

        // You can also test setters if desired:
        response.setMedName("UpdatedName");
        assertEquals("UpdatedName", response.getMedName());
    }
}
