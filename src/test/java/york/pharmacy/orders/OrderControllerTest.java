package york.pharmacy.orders;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import york.pharmacy.medicines.Medicine;
import york.pharmacy.orders.dto.OrderDeliveryDateResponse;
import york.pharmacy.orders.dto.OrderRequest;
import york.pharmacy.orders.dto.OrderResponse;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    private OrderController orderController;

    private Medicine medicine;
    private OrderRequest orderRequest;
    private OrderResponse orderResponse;

    @BeforeEach
    void setUp() {
        orderController = new OrderController(orderService);

        medicine = new Medicine(1L, "Jelly Bean", "J-01", Instant.now(), Instant.now());

        orderRequest = new OrderRequest(
                1L,
                100,
                LocalDate.of(2024, 12, 27)
        );

        orderResponse = new OrderResponse(
                1L,
                medicine,
                100,
                LocalDate.of(2024, 12, 27),
                OrderStatus.ORDERED,
                Instant.now(),
                Instant.now()
        );
    }

    /** Test: createOrder - Success */
    @Test
    void testCreateOrder_Success() {
        // Arrange
        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(orderResponse);

        // Act
        ResponseEntity<OrderResponse> response = orderController.createOrder(orderRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().getMedicine().getId());
        assertEquals("Jelly Bean", response.getBody().getMedicine().getName());
        assertEquals("J-01", response.getBody().getMedicine().getCode());
        verify(orderService, times(1)).createOrder(any(OrderRequest.class));
    }

    /** Test: batchCreateOrders - Success */
    @Test
    void testBatchCreateOrders_Success() {
        // Arrange
        List<OrderRequest> orderRequests = List.of(orderRequest);
        List<OrderResponse> orderResponses = List.of(orderResponse);
        when(orderService.batchCreateOrders(anyList())).thenReturn(orderResponses);

        // Act
        ResponseEntity<List<OrderResponse>> response = orderController.batchCreateOrders(orderRequests);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getMedicine().getId());
        assertEquals("Jelly Bean", response.getBody().get(0).getMedicine().getName());
        assertEquals("J-01", response.getBody().get(0).getMedicine().getCode());
        verify(orderService, times(1)).batchCreateOrders(anyList());
    }

    /** Test: getAllOrders - Success */
    @Test
    void testGetAllOrders_Success() {
        // Arrange
        when(orderService.getAllOrders()).thenReturn(List.of(orderResponse));

        // Act
        ResponseEntity<List<OrderResponse>> response = orderController.getAllOrders();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getMedicine().getId());
        assertEquals("Jelly Bean", response.getBody().get(0).getMedicine().getName());
        assertEquals("J-01", response.getBody().get(0).getMedicine().getCode());
        verify(orderService, times(1)).getAllOrders();
    }

    /** Test: getOrderById - Success */
    @Test
    void testGetOrderById_Success() {
        // Arrange
        when(orderService.getOrderById(1L)).thenReturn(orderResponse);

        // Act
        ResponseEntity<OrderResponse> response = orderController.getOrderById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getMedicine().getId());
        assertEquals("Jelly Bean", response.getBody().getMedicine().getName());
        assertEquals("J-01", response.getBody().getMedicine().getCode());
        verify(orderService, times(1)).getOrderById(1L);
    }

    /** Test: getDeliveryDatesByMedicineId - Success */
    @Test
    void testGetDeliveryDatesByMedicineId_Success() {
        // Arrange
        List<OrderDeliveryDateResponse> deliveryDates = List.of(
                new OrderDeliveryDateResponse(1L, LocalDate.of(2024, 12, 27)),
                new OrderDeliveryDateResponse(2L, LocalDate.of(2024, 12, 28))
        );

        when(orderService.getDeliveryDatesByMedicineId(1L)).thenReturn(deliveryDates);

        // Act
        ResponseEntity<List<OrderDeliveryDateResponse>> response = orderController.getDeliveryDatesByMedicineId(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getOrderId());
        assertEquals(LocalDate.of(2024, 12, 27), response.getBody().get(0).getDeliveryDate());
        assertEquals(2L, response.getBody().get(1).getOrderId());
        assertEquals(LocalDate.of(2024, 12, 28), response.getBody().get(1).getDeliveryDate());
        verify(orderService, times(1)).getDeliveryDatesByMedicineId(1L);
    }

    /** Test: updateOrder - Success */
    @Test
    void testUpdateOrder_Success() {
        // Arrange
        when(orderService.updateOrder(eq(1L), any(OrderRequest.class))).thenReturn(orderResponse);

        // Act
        ResponseEntity<OrderResponse> response = orderController.updateOrder(1L, orderRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getMedicine().getId());
        assertEquals("Jelly Bean", response.getBody().getMedicine().getName());
        assertEquals("J-01", response.getBody().getMedicine().getCode());
        verify(orderService, times(1)).updateOrder(eq(1L), any(OrderRequest.class));
    }

    @Test
    void testUpdateOrderStatusToReceived_Success() {
        // Arrange
        OrderResponse updatedOrderResponse = new OrderResponse(
                1L,
                medicine,
                orderRequest.getQuantity(),
                orderRequest.getDeliveryDate(),
                OrderStatus.RECEIVED,
                Instant.now(),
                Instant.now()
        );

        when(orderService.updateOrderStatusToReceived(1L)).thenReturn(updatedOrderResponse);

        // Act
        ResponseEntity<OrderResponse> response = orderController.updateOrderStatusToReceived(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        assertEquals(OrderStatus.RECEIVED, response.getBody().getStatus());
        verify(orderService, times(1)).updateOrderStatusToReceived(1L);

    }


    /** Test: deleteOrder - Success */
    @Test
    void testDeleteOrder_Success() {
        // Arrange
        doNothing().when(orderService).deleteOrder(1L);

        // Act
        ResponseEntity<Void> response = orderController.deleteOrder(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(orderService, times(1)).deleteOrder(1L);
    }
}
