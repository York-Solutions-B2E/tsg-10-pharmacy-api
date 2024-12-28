package york.pharmacy.medInventory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import york.pharmacy.medInventory.dto.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/med-inventory")
public class MedInventoryController {

    private final MedInventoryService medInventoryService;

    public MedInventoryController(MedInventoryService medInventoryService) {
        this.medInventoryService = medInventoryService;
    }

    // Create One
    @PostMapping
    public ResponseEntity<MedInventoryResponse> createOne(@RequestBody MedInventoryRequest request) {
        MedInventoryEntity entity = medInventoryService.createMedInventoryEntity(
                request.getMedName(),
                request.getStockCount(),
                request.getDeliveryDate()
        );

        // Convert entity to response
        MedInventoryResponse response = toResponse(entity);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Create Many
    @PostMapping("/bulk")
    public ResponseEntity<List<MedInventoryResponse>> createMany(@RequestBody List<MedInventoryRequest> requests) {
        // Service call
        List<MedInventoryEntity> entities = medInventoryService.createManyMedInventories(requests);

        // Convert each entity to response
        List<MedInventoryResponse> responses = entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return new ResponseEntity<>(responses, HttpStatus.CREATED);
    }

    // Read One
    @GetMapping("/{id}")
    public ResponseEntity<MedInventoryResponse> getOne(@PathVariable Long id) {
        MedInventoryEntity entity = medInventoryService.getMedInventoryEntityById(id);
        MedInventoryResponse response = toResponse(entity);
        return ResponseEntity.ok(response);
    }

    // Read Many
    @GetMapping
    public ResponseEntity<List<MedInventoryResponse>> getAll() {
        List<MedInventoryEntity> entities = medInventoryService.getAllMedInventoryEntities();
        List<MedInventoryResponse> responses = entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<MedInventoryResponse> update(
            @PathVariable Long id,
            @RequestBody MedInventoryRequest request
    ) {
        MedInventoryEntity updated = medInventoryService.updateMedInventoryEntity(
                id,
                request.getMedName(),
                request.getStockCount(),
                request.getDeliveryDate()
        );
        MedInventoryResponse response = toResponse(updated);
        return ResponseEntity.ok(response);
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        medInventoryService.deleteMedInventoryEntity(id);
        return ResponseEntity.noContent().build();
    }

    // Utility: Convert entity to response
    private MedInventoryResponse toResponse(MedInventoryEntity entity) {
        return MedInventoryResponse.builder()
                .id(entity.getId())
                .medName(entity.getMedName())
                .stockCount(entity.getStockCount())
                .deliveryDate(entity.getDeliveryDate())
                .build();
    }
}
