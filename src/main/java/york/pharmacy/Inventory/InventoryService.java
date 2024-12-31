package york.pharmacy.Inventory;

import org.springframework.stereotype.Service;
import york.pharmacy.exceptions.ResourceNotFoundException;
import york.pharmacy.Inventory.dto.InventoryRequest;
import york.pharmacy.Inventory.dto.InventoryResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public Inventory createInventory(InventoryRequest request) {
        Inventory entity = InventoryMapper.toEntity(request);
        return inventoryRepository.save(entity);
    }

    public List<Inventory> createManyInventories(List<InventoryRequest> requests) {
        List<Inventory> entities = requests.stream()
                .map(InventoryMapper::toEntity)
                .collect(Collectors.toList());
        return inventoryRepository.saveAll(entities);
    }

    public Inventory getInventoryById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));
    }

    public List<Inventory> getAllInventories() {
        return inventoryRepository.findAll();
    }

    public Inventory updateInventory(Long id, InventoryRequest request) {
        Inventory existingEntity = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));

        existingEntity.setMedicineId(request.getMedicineId());
        existingEntity.setStockQuantity(request.getStockQuantity());

        return inventoryRepository.save(existingEntity);
    }

    public void deleteInventory(Long id) {
        if (!inventoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inventory not found with id: " + id);
        }
        inventoryRepository.deleteById(id);
    }

    public InventoryResponse createInventoryAndReturn(InventoryRequest request) {
        Inventory savedEntity = createInventory(request);
        return InventoryMapper.toResponse(savedEntity);
    }
}