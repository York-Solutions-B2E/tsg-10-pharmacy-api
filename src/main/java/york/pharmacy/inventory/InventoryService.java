package york.pharmacy.inventory;

import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import york.pharmacy.exceptions.ResourceNotFoundException;
import york.pharmacy.exceptions.GlobalExceptionHandler;
import york.pharmacy.inventory.dto.InventoryRequest;
import york.pharmacy.inventory.dto.InventoryResponse;
import york.pharmacy.inventory.dto.InventoryUpdateRequest;
import york.pharmacy.medicines.Medicine;
import york.pharmacy.medicines.MedicineService;
import york.pharmacy.orders.Order;
import york.pharmacy.orders.OrderService;
import york.pharmacy.prescriptions.PrescriptionService;

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
    private final PrescriptionService prescriptionService;

    public InventoryService(InventoryRepository inventoryRepository, @Lazy OrderService orderService, MedicineService medicineService, PrescriptionService prescriptionService) {
        this.inventoryRepository = inventoryRepository;
        this.orderService = orderService;
        this.medicineService = medicineService;
        this.prescriptionService = prescriptionService;
    }

    public InventoryResponse createInventory(InventoryRequest request) {
        // Check if an Inventory with the same medicineId already exists
        if (inventoryRepository.findByMedicineId(request.getMedicineId()).isPresent()) {
            throw new DataIntegrityViolationException(
                    "Inventory already exists for medicineId=" + request.getMedicineId()
                            + ". Please use PUT to update instead."
            );
        }
        // Otherwise proceed
        Medicine medicine = medicineService.fetchMedicineById(request.getMedicineId());
        Inventory entity = InventoryMapper.toEntity(request, medicine);
        Inventory savedEntity = inventoryRepository.save(entity);
        return InventoryMapper.toResponse(savedEntity);
    }

    public List<InventoryResponse> createManyInventories(List<InventoryRequest> requests) {
        // For each request, check if there's already an existing row
        for (InventoryRequest req : requests) {
            if (inventoryRepository.findByMedicineId(req.getMedicineId()).isPresent()) {
                throw new DataIntegrityViolationException(
                        "Inventory already exists for medicineId=" + req.getMedicineId()
                                + ". Please use PUT to update instead."
                );
            }
        }
        List<Inventory> entities = requests.stream().map(request -> {
            Medicine medicine = medicineService.fetchMedicineById(request.getMedicineId());
            return InventoryMapper.toEntity(request, medicine);
        }).collect(Collectors.toList());

        List<Inventory> savedEntities = inventoryRepository.saveAll(entities);

        return savedEntities
                .stream()
                .map(InventoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    public InventoryResponse getInventoryById(Long id) {
        Inventory entity = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));

        int neededPills = prescriptionService.minOrderCount(entity.getMedicine().getId());

        int minOrderCount = 0;
        int difference = neededPills - entity.getStockQuantity();
        if (difference > 0) {
            minOrderCount = difference;
        }

        Optional<Order> closestOrder = orderService.getClosestOrderedDeliveryDate(entity.getId());

        InventoryResponse response = InventoryMapper.toResponse(entity, closestOrder);
        response.setMinimumOrderCount(minOrderCount);

        return response;
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

        // Do some logic to return a minimumOrderNumber
        // minimumOrderNumber = new+out_of_stock
        //

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