package york.pharmacy.orders;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import york.pharmacy.orders.dto.OrderRequest;
import york.pharmacy.orders.dto.OrderResponse;
import york.pharmacy.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    // Create a single order
    public OrderResponse createOrder(OrderRequest orderRequest) {
        Order order = OrderMapper.toEntity(orderRequest);
        Order savedOrder = orderRepository.save(order);
        return OrderMapper.toResponse(savedOrder);
    }

    // Create a batch of orders
    public List<OrderResponse> batchCreateOrders(List<OrderRequest> orderRequests) {
        List<Order> orders = orderRequests.stream()
                .map(OrderMapper::toEntity)
                .collect(Collectors.toList());
        List<Order> savedOrders = orderRepository.saveAll(orders);
        return savedOrders.stream()
                .map(OrderMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Get all orders
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(OrderMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Get an order by ID
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + id + " not found"));
        return OrderMapper.toResponse(order);
    }

    // Update an order by ID
    public OrderResponse updateOrder(Long id, OrderRequest orderRequest) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + id + " not found"));

        order.setMedicineId(orderRequest.getMedId());
        order.setQuantity(orderRequest.getQuantity());
        order.setDeliveryDate(orderRequest.getDeliveryDate());
        order.setStatus(OrderStatus.ORDERED); // Update status explicitly if needed
        Order updatedOrder = orderRepository.save(order);
        return OrderMapper.toResponse(updatedOrder);
    }

    // Delete an order by ID
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order with ID " + id + " not found");
        }
        orderRepository.deleteById(id);
    }
}
