package york.pharmacy.orders;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import york.pharmacy.inventory.Inventory;
import york.pharmacy.inventory.InventoryService;
import york.pharmacy.medicines.Medicine;
import york.pharmacy.orders.dto.OrderRequest;
import york.pharmacy.orders.dto.OrderResponse;
import york.pharmacy.exceptions.ResourceNotFoundException;
import york.pharmacy.utilities.ServiceUtility;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ServiceUtility serviceUtility;

    @InjectMocks
    private OrderService orderService;

    private Medicine medicine;
    private Inventory inventory;
    private Order order;
    private OrderRequest orderRequest;

    @BeforeEach
    void setUp() {

        medicine = new Medicine(1L, "Jelly Beans", "J-01", Instant.now(), Instant.now());
        inventory = new Inventory(1L, medicine, 500);

        order = new Order(
                1L,
                inventory,
                100,
                LocalDate.of(2024, 12, 27),
                OrderStatus.ORDERED,
                Instant.now(),
                Instant.now()
        );

        orderRequest = new OrderRequest(
                1L,
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
        assertEquals(1L, result.getInventory().getId());
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
        assertEquals(1L, result.get(0).getInventory().getId());
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
        assertEquals(1L, result.get(0).getInventory().getId());
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
        assertEquals(1L, result.getInventory().getId());
        verify(orderRepository, times(1)).findById(1L);
    }

    /** Test: updateOrder - Success */
    @Test
    void testUpdateOrder_Success() {
        // Arrange
        OrderRequest updatedRequest = new OrderRequest(
                1L,
                200,
                LocalDate.of(2024, 12, 28)
        );

        Order updatedOrder = new Order(
                1L,
                inventory,
                200,
                LocalDate.of(2024, 12, 28),
                OrderStatus.ORDERED,
                Instant.now(),
                Instant.now().plus(Duration.ofMinutes(5))
        );

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        // Act
        OrderResponse result = orderService.updateOrder(1L, updatedRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getInventory().getId());
        assertEquals(200, result.getQuantity());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    /** Test: updateOrderStatusToReceived - Success */
    @Test
    void testUpdateOrderStatusToReceived_Success() {
        // Arrange
        Order updatedOrder = new Order(
                1L,
                inventory,
                order.getQuantity(),
                order.getDeliveryDate(),
                OrderStatus.RECEIVED, // Updated status
                order.getCreatedAt(),
                Instant.now() // Updated timestamp
        );

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        // Act
        OrderResponse response = orderService.updateOrderStatusToReceived(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(OrderStatus.RECEIVED, response.getStatus());
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
        orderService.deleteOrder(1L);

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