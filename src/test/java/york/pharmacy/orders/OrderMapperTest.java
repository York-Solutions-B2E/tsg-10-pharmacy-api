package york.pharmacy.orders;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import york.pharmacy.medicines.Medicine;
import york.pharmacy.orders.dto.OrderRequest;
import york.pharmacy.orders.dto.OrderResponse;

import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {

    private Medicine medicine;
    private Order order;
    private OrderRequest orderRequest;

    @BeforeEach
    void setUp() {
        medicine = new Medicine(1L, "Jelly Beans", "J-01", Instant.now(), Instant.now());
        orderRequest = new OrderRequest(1L, 100, LocalDate.of(2024, 12, 27));
        order = new Order(
                1L,
                medicine,
                100,
                LocalDate.of(2025, 2, 1),
                OrderStatus.ORDERED,
                Instant.now(),
                Instant.now()
        );
    }

    @Test
    void testToEntity() {
        // Act
        Order mappedOrder = OrderMapper.toEntity(orderRequest, medicine);

        // Assert
        assertNotNull(mappedOrder);
        assertEquals(medicine, mappedOrder.getMedicine());
        assertEquals(100, mappedOrder.getQuantity());
        assertEquals(LocalDate.of(2024, 12, 27), mappedOrder.getDeliveryDate());
        assertEquals(OrderStatus.ORDERED, mappedOrder.getStatus());
    }

    @Test
    void testToResponse() {
        // Act
        OrderResponse response = OrderMapper.toResponse(order);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(medicine, response.getMedicine());
        assertEquals(100, response.getQuantity());
        assertEquals(LocalDate.of(2025, 2, 1), response.getDeliveryDate());
        assertEquals(OrderStatus.ORDERED, response.getStatus());
        assertEquals(order.getCreatedAt(), response.getCreatedAt());
        assertEquals(order.getUpdatedAt(), response.getUpdatedAt());
    }
}
