package york.pharmacy.orders;

import org.junit.jupiter.api.Test;
import york.pharmacy.orders.dto.OrderRequest;
import york.pharmacy.orders.dto.OrderResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {

    @Test
    void testToEntity() {
        // Arrange
        OrderRequest orderRequest = new OrderRequest("123456", 100, LocalDate.of(2024, 12, 27));

        // Act
        Orders order = OrderMapper.toEntity(orderRequest);

        // Assert
        assertNotNull(order);
        assertEquals("123456", order.getMedicineId());
        assertEquals(100, order.getQuantity());
        assertEquals(LocalDate.of(2024, 12, 27), order.getDeliveryDate());
        assertEquals(OrderStatus.ORDERED, order.getStatus());
    }

    @Test
    void testToResponse() {
        // Arrange
        Orders order = new Orders(
                1L,
                "123456",
                100,
                LocalDate.of(2025, 2, 1),
                OrderStatus.ORDERED,
                LocalDateTime.of(2024, 12, 26, 10, 0),
                LocalDateTime.of(2024, 12, 26, 10, 0)
        );

        // Act
        OrderResponse response = OrderMapper.toResponse(order);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("123456", response.getMedId());
        assertEquals(100, response.getQuantity());
        assertEquals(LocalDate.of(2025, 2, 1), response.getDeliveryDate());
        assertEquals(OrderStatus.ORDERED, response.getStatus());
        assertEquals(LocalDateTime.of(2024, 12, 26, 10, 0), response.getCreatedAt());
        assertEquals(LocalDateTime.of(2024, 12, 26, 10, 0), response.getUpdatedAt());
    }

}
