package york.pharmacy.inventory;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import york.pharmacy.inventory.dto.InventoryRequest;
import york.pharmacy.inventory.dto.InventoryResponse;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public ResponseEntity<InventoryResponse> createOne(@Valid @RequestBody InventoryRequest request) {
        InventoryResponse response = inventoryService.createInventory(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<InventoryResponse>> createMany(@Valid @RequestBody List<InventoryRequest> requests) {
        List<InventoryResponse> responses = inventoryService.createManyInventories(requests);
        return new ResponseEntity<>(responses, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponse> getOne(@PathVariable @Min(1) Long id) {
        InventoryResponse response = inventoryService.getInventoryById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getAll() {
        List<InventoryResponse> responses = inventoryService.getAllInventories();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryResponse> update(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody InventoryRequest request
    ) {
        InventoryResponse response = inventoryService.updateInventory(id, request);
        return ResponseEntity.ok(response);
    }

    // Endpoint for pharmacists to manually add an amount of pills,
    // and for "prescriptions" and "orders" tables to auto-adjust
    @PutMapping("/{id}/adjust-stock/{pillAdjustment}")
    public ResponseEntity<InventoryResponse> adjustStockQuantity(
            @PathVariable Long id,
            @PathVariable Integer pillAdjustment) {
        InventoryResponse response = inventoryService.adjustStockQuantity(id, pillAdjustment);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Min(1) Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }
}