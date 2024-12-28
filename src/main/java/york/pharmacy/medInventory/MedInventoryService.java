package york.pharmacy.medInventory;

import org.springframework.stereotype.Service;
import york.pharmacy.medInventory.dto.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MedInventoryService {

    private final MedInventoryRepository medInventoryRepository;

    public MedInventoryService(MedInventoryRepository medInventoryRepository) {
        this.medInventoryRepository = medInventoryRepository;
    }

    public MedInventoryEntity createMedInventoryEntity(String medName, int stockCount, LocalDate deliveryDate) {
        // 1) Build the entity
        MedInventoryEntity entity = MedInventoryEntity.builder()
                .medName(medName)
                .stockCount(stockCount)
                .deliveryDate(deliveryDate)
                .build();

        // 2) Save it
        return medInventoryRepository.save(entity);
    }

    public List<MedInventoryEntity> createManyMedInventories(List<MedInventoryRequest> requests) {
        // Convert each request into an entity
        List<MedInventoryEntity> entities = new ArrayList<>();
        for (MedInventoryRequest req : requests) {
            MedInventoryEntity e = createMedInventoryEntity(req.getMedName(), req.getStockCount(), req.getDeliveryDate());
            entities.add(e);
        }
        return entities;
    }


    public MedInventoryEntity getMedInventoryEntityById(Long id) {
        // 1) Retrieve from DB
        return medInventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No MedInventory found with id: " + id));
    }

    public List<MedInventoryEntity> getAllMedInventoryEntities() {
        // 1) Retrieve all
        return medInventoryRepository.findAll();
    }

    public MedInventoryEntity updateMedInventoryEntity(Long id, String newName, int newStockCount, LocalDate newDeliveryDate) {
        // 1) Find existing entity
        MedInventoryEntity existingEntity = medInventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No MedInventory found with id: " + id));

        // 2) Update its fields
        existingEntity.setMedName(newName);
        existingEntity.setStockCount(newStockCount);
        existingEntity.setDeliveryDate(newDeliveryDate);

        // 3) Save updated
        return medInventoryRepository.save(existingEntity);
    }

    public void deleteMedInventoryEntity(Long id) {
        // 1) Check existence
        if (!medInventoryRepository.existsById(id)) {
            throw new RuntimeException("No MedInventory found with id: " + id);
        }
        // 2) Delete
        medInventoryRepository.deleteById(id);
    }
}
