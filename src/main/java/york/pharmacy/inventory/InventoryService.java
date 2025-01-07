package york.pharmacy.inventory;

import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import york.pharmacy.exceptions.ResourceNotFoundException;
import york.pharmacy.inventory.dto.InventoryRequest;
import york.pharmacy.inventory.dto.InventoryResponse;
import york.pharmacy.inventory.dto.InventoryUpdateRequest;
import york.pharmacy.medicines.Medicine;
import york.pharmacy.medicines.MedicineService;
import york.pharmacy.orders.Order;
import york.pharmacy.orders.OrderService;
import york.pharmacy.prescriptions.PrescriptionServiceAdapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final OrderService orderService;
    private final MedicineService medicineService;

    // Use the interface, not the concrete class
    private final PrescriptionServiceAdapter prescriptionServiceAdapter;

    public InventoryService(
            InventoryRepository inventoryRepository,
            @Lazy OrderService orderService,
            MedicineService medicineService,
            PrescriptionServiceAdapter prescriptionServiceAdapter
    ) {
        this.inventoryRepository = inventoryRepository;
        this.orderService = orderService;
        this.medicineService = medicineService;
        this.prescriptionServiceAdapter = prescriptionServiceAdapter;
    }

    public InventoryResponse createInventory(InventoryRequest request) {
        if (inventoryRepository.findByMedicineId(request.getMedicineId()).isPresent()) {
            throw new DataIntegrityViolationException(
                    "Inventory already exists for medicineId=" + request.getMedicineId()
                            + ". Please use PUT to update instead."
            );
        }
        Medicine medicine = medicineService.fetchMedicineById(request.getMedicineId());
        Inventory entity = InventoryMapper.toEntity(request, medicine);
        Inventory savedEntity = inventoryRepository.save(entity);
        return InventoryMapper.toResponse(savedEntity);
    }

    public List<InventoryResponse> createManyInventories(List<InventoryRequest> requests) {
        for (InventoryRequest req : requests) {
            if (inventoryRepository.findByMedicineId(req.getMedicineId()).isPresent()) {
                throw new DataIntegrityViolationException(
                        "Inventory already exists for medicineId=" + req.getMedicineId()
                                + ". Please use PUT to update instead."
                );
            }
        }
        List<Inventory> entities = requests.stream()
                .map(request -> {
                    Medicine medicine = medicineService.fetchMedicineById(request.getMedicineId());
                    return InventoryMapper.toEntity(request, medicine);
                })
                .collect(Collectors.toList());

        List<Inventory> savedEntities = inventoryRepository.saveAll(entities);
        return savedEntities.stream()
                .map(InventoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    public InventoryResponse getInventoryById(Long id) {
        Inventory entity = inventoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Inventory not found with id: " + id));

        // Use the adapter method
        int neededPills = prescriptionServiceAdapter.minOrderCount(entity.getMedicine().getId());
        int minOrderCount = Math.max(0, neededPills - entity.getStockQuantity());
        boolean sufficientStock = entity.getStockQuantity() >= neededPills;

        Optional<Order> closestOrder = orderService.getClosestOrderedDeliveryDate(entity.getId());

        InventoryResponse response = InventoryMapper.toResponse(entity, closestOrder);
        response.setMinimumOrderCount(minOrderCount);
        response.setSufficientStock(sufficientStock);

        return response;
    }

    public List<InventoryResponse> getAllInventories() {
        List<Inventory> entities = inventoryRepository.findAll();
        return entities.stream()
                .map(entity -> {
                    // Note: replaced prescriptionService with prescriptionServiceAdapter
                    int neededPills = prescriptionServiceAdapter.minOrderCount(entity.getMedicine().getId());
                    int minOrderCount = Math.max(0, neededPills - entity.getStockQuantity());
                    boolean sufficientStock = entity.getStockQuantity() >= neededPills;

                    Optional<Order> closestOrder = orderService.getClosestOrderedDeliveryDate(entity.getId());
                    InventoryResponse response = InventoryMapper.toResponse(entity, closestOrder);

                    response.setMinimumOrderCount(minOrderCount);
                    response.setSufficientStock(sufficientStock);

                    return response;
                })
                .collect(Collectors.toList());
    }

    public InventoryResponse updateInventory(Long id, InventoryRequest request) {
        Inventory existingEntity = inventoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Inventory not found with id: " + id));

        existingEntity.setStockQuantity(request.getStockQuantity());
        Inventory updatedEntity = inventoryRepository.save(existingEntity);
        return InventoryMapper.toResponse(updatedEntity);
    }

    public InventoryResponse updateInventoryStock(Long id, InventoryUpdateRequest request) {
        Inventory existingEntity = inventoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Inventory not found with id: " + id));

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

    public InventoryResponse adjustStockQuantity(Long id, Integer pillAdjustment) {
        Inventory existingEntity = inventoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Inventory not found with id: " + id));

        int newQuantity = existingEntity.getStockQuantity() + pillAdjustment;
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
                .orElseThrow(() ->
                        new ResourceNotFoundException("Inventory with ID " + id + " not found"));
    }
}