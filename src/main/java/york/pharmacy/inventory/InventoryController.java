package york.pharmacy.inventory;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import york.pharmacy.inventory.dto.InventoryRequest;
import york.pharmacy.inventory.dto.InventoryResponse;
import york.pharmacy.inventory.dto.InventoryUpdateRequest;
import york.pharmacy.utilities.ServiceUtility;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    private final ServiceUtility serviceUtility;

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
            @Valid @RequestBody InventoryUpdateRequest request
    ) {
        InventoryResponse response = inventoryService.updateInventoryStock(id, request);

        return ResponseEntity.ok(response);
    }

    // Endpoint for pharmacists to manually add an amount of pills,
    // and for "prescriptions" and "orders" tables to auto-adjust;
    // Front-end doesn't use this endpoint, so can probably delete
    @PutMapping("/{id}/adjust-stock/{pillAdjustment}")
    public ResponseEntity<InventoryResponse> adjustStockQuantity(
            @PathVariable Long id,
            @PathVariable Integer pillAdjustment) {
        // Update stock in db
        InventoryResponse response = inventoryService.adjustStockQuantity(id, pillAdjustment);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Min(1) Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }
}