package york.pharmacy.orders;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import york.pharmacy.orders.dto.OrderDeliveryDateResponse;
import york.pharmacy.orders.dto.OrderRequest;
import york.pharmacy.orders.dto.OrderResponse;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Create a new order
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        OrderResponse orderResponse = orderService.createOrder(orderRequest);
        return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
    }

    // Create multiple orders (batch)
    @PostMapping("/batch")
    public ResponseEntity<List<OrderResponse>> batchCreateOrders(@Valid @RequestBody List<OrderRequest> orderRequests) {
        List<OrderResponse> orderResponses = orderService.batchCreateOrders(orderRequests);
        return new ResponseEntity<>(orderResponses, HttpStatus.CREATED);
    }

    // Get all orders
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    // Get an order by its ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        OrderResponse orderResponse = orderService.getOrderById(id);
        return new ResponseEntity<>(orderResponse, HttpStatus.OK);
    }

    @GetMapping("/delivery-dates/{medicineId}")
    public ResponseEntity<List<OrderDeliveryDateResponse>> getDeliveryDatesByMedicineId(@PathVariable Long medicineId) {
        List<OrderDeliveryDateResponse> deliveryDates = orderService.getDeliveryDatesByMedicineId(medicineId);
        return new ResponseEntity<>(deliveryDates, HttpStatus.OK);
    }

    // Update an existing order by its ID
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(@PathVariable Long id, @Valid @RequestBody OrderRequest orderRequest) {
        OrderResponse orderResponse = orderService.updateOrder(id, orderRequest);
        return new ResponseEntity<>(orderResponse, HttpStatus.OK);
    }

    // Update an existing order's status to "RECEIVED" by its ID
    @PutMapping("/received/{id}")
    public ResponseEntity<OrderResponse> updateOrderStatusToReceived(@PathVariable Long id) {
        OrderResponse orderResponse = orderService.updateOrderStatusToReceived(id);
        return new ResponseEntity<>(orderResponse, HttpStatus.OK);
    }

    // Delete an order by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
