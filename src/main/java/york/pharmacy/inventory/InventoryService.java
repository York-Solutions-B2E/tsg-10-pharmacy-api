package york.pharmacy.inventory;

import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import york.pharmacy.exceptions.ResourceNotFoundException;
import york.pharmacy.inventory.dto.InventoryRequest;
import york.pharmacy.inventory.dto.InventoryResponse;
import york.pharmacy.inventory.dto.InventoryUpdateRequest;
import york.pharmacy.medicines.Medicine;
import york.pharmacy.medicines.MedicineService;
import york.pharmacy.orders.Order;
import york.pharmacy.orders.OrderService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final OrderService orderService;
    private final MedicineService medicineService;

    public InventoryService(InventoryRepository inventoryRepository, @Lazy OrderService orderService, MedicineService medicineService) {
        this.inventoryRepository = inventoryRepository;
        this.orderService = orderService;
        this.medicineService = medicineService;
    }

    public InventoryResponse createInventory(InventoryRequest request) {
        Medicine medicine = medicineService.fetchMedicineById(request.getMedicineId()); // Fetch Medicine entity
        Inventory entity = InventoryMapper.toEntity(request, medicine);
        Inventory savedEntity = inventoryRepository.save(entity);
        return InventoryMapper.toResponse(savedEntity);
    }

    public List<InventoryResponse> createManyInventories(List<InventoryRequest> requests) {
        List<Inventory> entities = requests.stream().map(request -> {
            Medicine medicine = medicineService.fetchMedicineById(request.getMedicineId());
            return InventoryMapper.toEntity(request, medicine);
        }).collect(Collectors.toList());
        List<Inventory> savedEntities = inventoryRepository.saveAll(entities);
        return savedEntities.stream().map(InventoryMapper::toResponse).collect(Collectors.toList());
    }

    public InventoryResponse getInventoryById(Long id) {
        Inventory entity = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));

        // Fetch the closest delivery date for the associated medicine
        Optional<Order> closestOrder = orderService.getClosestOrderedDeliveryDate(entity.getId());

        return InventoryMapper.toResponse(entity, closestOrder);
    }


    public List<InventoryResponse> getAllInventories() {
        List<Inventory> entities = inventoryRepository.findAll();
        return entities.stream().map(entity -> {
            Optional<Order> closestOrder = orderService.getClosestOrderedDeliveryDate(entity.getId());
            return InventoryMapper.toResponse(entity, closestOrder);
        }).collect(Collectors.toList());
    }

    public InventoryResponse updateInventory(Long id, InventoryRequest request) {
        Inventory existingEntity = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));

        existingEntity.setStockQuantity(request.getStockQuantity());

        Inventory updatedEntity = inventoryRepository.save(existingEntity);
        return InventoryMapper.toResponse(updatedEntity);
    }

    public InventoryResponse updateInventoryStock(Long id, InventoryUpdateRequest request) {
        Inventory existingEntity = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));

        existingEntity.setStockQuantity(request.getStockQuantity());

        Inventory updatedEntity = inventoryRepository.save(existingEntity);
        return InventoryMapper.toResponse(updatedEntity);
    }

    public void deleteInventory(Long id) {
        if (!inventoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inventory not found with id: " + id);
        }
        inventoryRepository.deleteById(id);
    }

    // Method for Yara's "prescriptions" table to use to check if there is sufficient stock
    public InventoryResponse updateSufficientStock(HashMap<Long, Integer> medicineCount) {
        // Get the first (and only) entry
        Map.Entry<Long, Integer> entry = medicineCount.entrySet().iterator().next();
        Long medicineId = entry.getKey();
        Integer requiredPills = entry.getValue();

        // Find inventory by medicineId
        Inventory existingEntity = inventoryRepository.findByMedicineId(medicineId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for medicine id: " + medicineId));

        boolean isSufficient = existingEntity.getStockQuantity() >= requiredPills;
        existingEntity.setSufficientStock(isSufficient);

        Inventory updatedEntity = inventoryRepository.save(existingEntity);
        return InventoryMapper.toResponse(updatedEntity);
    }

    // Method for Yara's "prescriptions" table to use to subtract from stockQuantity (negative number)
    // or for Rodrigo's "orders" table to use to add (positive number)
    public InventoryResponse adjustStockQuantity(Long id, Integer pillAdjustment) {
        Inventory existingEntity = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));

        // Add pills (positive adjustment) or remove pills (negative adjustment)
        int newQuantity = existingEntity.getStockQuantity() + pillAdjustment;

        // Prevent negative stock
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Cannot reduce stock below 0");
        }

        existingEntity.setStockQuantity(newQuantity);

        Inventory updatedEntity = inventoryRepository.save(existingEntity);
        return InventoryMapper.toResponse(updatedEntity);
    }

    // Helper method - Used in service layer to fetch Inventory entity by ID
    public Inventory fetchInventoryById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory with ID " + id + " not found"));
    }

}