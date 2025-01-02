package york.pharmacy.inventory;

import org.springframework.stereotype.Service;
import york.pharmacy.exceptions.ResourceNotFoundException;
import york.pharmacy.inventory.dto.InventoryRequest;
import york.pharmacy.inventory.dto.InventoryResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public InventoryResponse createInventory(InventoryRequest request) {
        Inventory entity = InventoryMapper.toEntity(request);
        Inventory savedEntity = inventoryRepository.save(entity);
        return InventoryMapper.toResponse(savedEntity);
    }

    public List<InventoryResponse> createManyInventories(List<InventoryRequest> requests) {
        List<Inventory> entities = requests.stream()
                .map(InventoryMapper::toEntity)
                .collect(Collectors.toList());
        List<Inventory> savedEntities = inventoryRepository.saveAll(entities);
        return savedEntities.stream()
                .map(InventoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    public InventoryResponse getInventoryById(Long id) {
        Inventory entity = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));
        return InventoryMapper.toResponse(entity);
    }

    public List<InventoryResponse> getAllInventories() {
        List<Inventory> entities = inventoryRepository.findAll();
        return entities.stream()
                .map(InventoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    public InventoryResponse updateInventory(Long id, InventoryRequest request) {
        Inventory existingEntity = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));

        // Remove the line that sets medicineId - it should remain unchanged
        existingEntity.setStockQuantity(request.getStockQuantity());
        existingEntity.setSufficientStock(request.getSufficientStock());

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
}