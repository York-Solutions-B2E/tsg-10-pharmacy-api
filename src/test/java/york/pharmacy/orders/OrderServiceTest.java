package york.pharmacy.orders;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import york.pharmacy.orders.dto.OrderRequest;
import york.pharmacy.orders.dto.OrderResponse;
import york.pharmacy.exceptions.ResourceNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private OrderRequest orderRequest;

    @BeforeEach
    void setUp() {
        order = new Order(
                1L,
                "123456",
                100,
                LocalDate.of(2024, 12, 27),
                OrderStatus.ORDERED,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        orderRequest = new OrderRequest(
                "123456",
                100,
                LocalDate.of(2024, 12, 27)
        );
    }

    /** Test: createOrder - Success */
    @Test
    void testCreateOrder_Success() {
        // Arrange
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        OrderResponse result = orderService.createOrder(orderRequest);

        // Assert
        assertNotNull(result);
        assertEquals("123456", result.getMedId());
        assertEquals(100, result.getQuantity());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    /** Test: batchCreateOrders - Success */
    @Test
    void testBatchCreateOrders_Success() {
        // Arrange
        List<OrderRequest> orderRequests = List.of(orderRequest);
        List<Order> orders = List.of(order);
        when(orderRepository.saveAll(anyList())).thenReturn(orders);

        // Act
        List<OrderResponse> result = orderService.batchCreateOrders(orderRequests);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("123456", result.get(0).getMedId());
        verify(orderRepository, times(1)).saveAll(anyList());
    }

    /** Test: getAllOrders - Success */
    @Test
    void testGetAllOrders_Success() {
        // Arrange
        when(orderRepository.findAll()).thenReturn(List.of(order));

        // Act
        List<OrderResponse> result = orderService.getAllOrders();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("123456", result.get(0).getMedId());
        verify(orderRepository, times(1)).findAll();
    }

    /** Test: getOrderById - Success */
    @Test
    void testGetOrderById_Success() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act
        OrderResponse result = orderService.getOrderById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("123456", result.getMedId());
        verify(orderRepository, times(1)).findById(1L);
    }

    /** Test: getOrderById - Not Found */
    @Test
    void testGetOrderById_NotFound() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(1L));
        verify(orderRepository, times(1)).findById(1L);
    }

    /** Test: updateOrder - Success */
    @Test
    void testUpdateOrder_Success() {
        // Arrange
        OrderRequest updatedRequest = new OrderRequest(
                "789012",
                200,
                LocalDate.of(2024, 12, 28)
        );

        Order updatedOrder = new Order(
                1L,
                "789012",
                200,
                LocalDate.of(2024, 12, 28),
                OrderStatus.ORDERED,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        // Act
        OrderResponse result = orderService.updateOrder(1L, updatedRequest);

        // Assert
        assertNotNull(result);
        assertEquals("789012", result.getMedId());
        assertEquals(200, result.getQuantity());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    /** Test: deleteOrder - Success */
    @Test
    void testDeleteOrder_Success() {
        // Arrange
        when(orderRepository.existsById(1L)).thenReturn(true);
        doNothing().when(orderRepository).deleteById(1L);

        // Act
        assertDoesNotThrow(() -> orderService.deleteOrder(1L));

        // Assert
        verify(orderRepository, times(1)).existsById(1L);
        verify(orderRepository, times(1)).deleteById(1L);
    }

    /** Test: deleteOrder - Not Found */
    @Test
    void testDeleteOrder_NotFound() {
        // Arrange
        when(orderRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> orderService.deleteOrder(1L));
        verify(orderRepository, times(1)).existsById(1L);
        verify(orderRepository, never()).deleteById(anyLong());
    }
}
