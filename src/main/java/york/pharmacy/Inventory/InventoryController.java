package york.pharmacy.Inventory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import york.pharmacy.Inventory.dto.InventoryRequest;
import york.pharmacy.Inventory.dto.InventoryResponse;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public ResponseEntity<InventoryResponse> createOne(@RequestBody InventoryRequest request) {
        Inventory savedEntity = inventoryService.createInventory(request);
        InventoryResponse response = InventoryMapper.toResponse(savedEntity);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<InventoryResponse>> createMany(@RequestBody List<InventoryRequest> requests) {
        List<Inventory> savedEntities = inventoryService.createManyInventories(requests);
        List<InventoryResponse> responses = savedEntities.stream()
                .map(InventoryMapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(responses, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponse> getOne(@PathVariable Long id) {
        Inventory entity = inventoryService.getInventoryById(id);
        InventoryResponse response = InventoryMapper.toResponse(entity);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getAll() {
        List<Inventory> entities = inventoryService.getAllInventories();
        List<InventoryResponse> responses = entities.stream()
                .map(InventoryMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryResponse> update(
            @PathVariable Long id,
            @RequestBody InventoryRequest request
    ) {
        Inventory updatedEntity = inventoryService.updateInventory(id, request);
        InventoryResponse response = InventoryMapper.toResponse(updatedEntity);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }
}