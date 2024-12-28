package york.pharmacy.medInventory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for MedInventory entity using Spring Data JPA
 * and an in-memory H2 database.
 */
@DataJpaTest
class MedInventoryEntityTest {

    @Autowired
    private MedInventoryRepository medInventoryRepository;

    @Test
    @DisplayName("Test saving and retrieving a MedInventory entity")
    void testInsertAndFind() {
        // 1. Create a new MedInventory entity (assuming Lombok @Builder, etc.).
        //    Adjust as necessary if your entity uses constructors/setters instead.
        MedInventoryEntity medInventory = MedInventoryEntity.builder()
                .medName("TestMed")
                .stockCount(10)
                .deliveryDate(LocalDate.now().plusDays(7))
                .build();

        // 2. Save to the in-memory database
        MedInventoryEntity saved = medInventoryRepository.save(medInventory);
        assertNotNull(saved.getId(), "After saving, the entity should have an ID");

        // 3. Retrieve by ID and verify
        Optional<MedInventoryEntity> found = medInventoryRepository.findById(saved.getId());
        assertTrue(found.isPresent(), "Entity should be found");
        assertEquals("TestMed", found.get().getMedName());
        assertEquals(10, found.get().getStockCount());
    }

    @Test
    @DisplayName("Test updating an existing MedInventory entity")
    void testUpdate() {
        // 1. Insert a new entity
        MedInventoryEntity medInventory = MedInventoryEntity.builder()
                .medName("OriginalName")
                .stockCount(5)
                .build();

        MedInventoryEntity saved = medInventoryRepository.save(medInventory);
        Long savedId = saved.getId();
        assertNotNull(savedId, "Saved entity must have a non-null ID");

        // 2. Update fields
        saved.setMedName("UpdatedName");
        saved.setStockCount(20);
        medInventoryRepository.save(saved); // save again with updated data

        // 3. Retrieve and confirm changes
        Optional<MedInventoryEntity> updated = medInventoryRepository.findById(savedId);
        assertTrue(updated.isPresent(), "Updated entity should still exist");
        assertEquals("UpdatedName", updated.get().getMedName());
        assertEquals(20, updated.get().getStockCount());
    }

    @Test
    @DisplayName("Test deleting a MedInventory entity")
    void testDelete() {
        // 1. Insert a new entity
        MedInventoryEntity medInventory = MedInventoryEntity.builder()
                .medName("ToBeDeleted")
                .stockCount(50)
                .build();

        MedInventoryEntity saved = medInventoryRepository.save(medInventory);
        Long savedId = saved.getId();
        assertNotNull(savedId, "Entity must have an ID after saving");

        // 2. Delete the entity
        medInventoryRepository.deleteById(savedId);

        // 3. Verify it's gone
        Optional<MedInventoryEntity> deleted = medInventoryRepository.findById(savedId);
        assertFalse(deleted.isPresent(), "Entity should no longer be in the database");
    }
}
