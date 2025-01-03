package york.pharmacy.orders;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import york.pharmacy.medicines.Medicine;
import york.pharmacy.medicines.MedicineService;
import york.pharmacy.orders.dto.OrderDeliveryDateResponse;
import york.pharmacy.orders.dto.OrderRequest;
import york.pharmacy.orders.dto.OrderResponse;
import york.pharmacy.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MedicineService medicineService;

    // Create a single order
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        Medicine medicine = medicineService.fetchMedicineById(orderRequest.getMedicineId());
        Order order = OrderMapper.toEntity(orderRequest, medicine);
        Order savedOrder = orderRepository.save(order);
        return OrderMapper.toResponse(savedOrder);
    }

    // Create a batch of orders
    @Transactional
    public List<OrderResponse> batchCreateOrders(List<OrderRequest> orderRequests) {
        List<Order> orders = orderRequests.stream().map(request -> {
            Medicine medicine = medicineService.fetchMedicineById(request.getMedicineId());
            return OrderMapper.toEntity(request, medicine);
        }).collect(Collectors.toList());
        List<Order> savedOrders = orderRepository.saveAll(orders);
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

        Medicine medicine = medicineService.fetchMedicineById(orderRequest.getMedicineId());

        order.setMedicine(medicine);
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

    // Helper Method - Return Delivery Date Based on Medicine ID (Filtered by "ORDERED" Status)
    public List<OrderDeliveryDateResponse> getDeliveryDatesByMedicineId(Long medicineId) {
        List<Order> orders = orderRepository.findByMedicineIdAndStatus(medicineId, OrderStatus.ORDERED)
                .orElseThrow(() -> new ResourceNotFoundException("No 'ORDERED' status orders found for Medicine with ID " + medicineId));

        return orders.stream()
                .map(order -> new OrderDeliveryDateResponse(order.getId(), order.getDeliveryDate()))
                .collect(Collectors.toList());
    }

}
