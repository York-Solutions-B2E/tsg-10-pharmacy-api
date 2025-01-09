package york.pharmacy.orders;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import york.pharmacy.inventory.Inventory;
import york.pharmacy.inventory.InventoryService;
import york.pharmacy.orders.dto.OrderRequest;
import york.pharmacy.orders.dto.OrderResponse;
import york.pharmacy.exceptions.ResourceNotFoundException;
import york.pharmacy.utilities.ServiceUtility;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ServiceUtility serviceUtility;

    // Create a single order
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        Inventory inventory = serviceUtility.fetchInventoryById(orderRequest.getInventoryId());

        Order order = OrderMapper.toEntity(orderRequest, inventory);
        Order savedOrder = orderRepository.save(order);

        serviceUtility.updateAwaitingShipmentStatus(savedOrder);

        return OrderMapper.toResponse(savedOrder);
    }

    // Create a batch of orders
    @Transactional
    public List<OrderResponse> batchCreateOrders(List<OrderRequest> orderRequests) {
        List<Order> orders = orderRequests.stream().map(request -> {
            Inventory inventory = serviceUtility.fetchInventoryById(request.getInventoryId());
            return OrderMapper.toEntity(request, inventory);
        }).collect(Collectors.toList());
        List<Order> savedOrders = orderRepository.saveAll(orders);

        savedOrders.forEach(serviceUtility::updateAwaitingShipmentStatus);

        return savedOrders.stream().map(OrderMapper::toResponse).collect(Collectors.toList());
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
    @Transactional
    public OrderResponse updateOrder(Long id, OrderRequest orderRequest) {
        // Fetch the existing order
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + id + " not found"));

        order.setQuantity(orderRequest.getQuantity());
        order.setDeliveryDate(orderRequest.getDeliveryDate());
        order.setStatus(orderRequest.getStatus());
        Order updatedOrder = orderRepository.save(order);
        return OrderMapper.toResponse(updatedOrder);
    }

    // Update Order to Received status
    public OrderResponse updateOrderStatusToReceived(Long id) {
        // Fetch the existing order
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + id + " not found"));

        order.setStatus(OrderStatus.RECEIVED);
        Order updatedOrder = orderRepository.save(order);

        serviceUtility.updateStockReceivedStatus(updatedOrder);
        serviceUtility.adjustStockQuantity(updatedOrder.getInventory().getId(), updatedOrder.getQuantity());

        return OrderMapper.toResponse(updatedOrder);
    }

    // Delete an order by ID
    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order with ID " + id + " not found");
        }
        orderRepository.deleteById(id);
    }

}
