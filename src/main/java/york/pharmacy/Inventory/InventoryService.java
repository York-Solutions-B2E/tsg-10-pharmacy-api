package york.pharmacy.inventory;

import org.springframework.stereotype.Service;
import york.pharmacy.exceptions.ResourceNotFoundException;
import york.pharmacy.inventory.dto.InventoryRequest;
import york.pharmacy.inventory.dto.InventoryResponse;

import java.util.List;
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

        existingEntity.setMedicineId(request.getMedicineId());
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
}