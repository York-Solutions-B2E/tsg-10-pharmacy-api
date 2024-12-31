package york.pharmacy.Inventory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class InventoryTest {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Test
    @DisplayName("Test saving and retrieving an Inventory entity")
    void testInsertAndFind() {
        Inventory inventory = Inventory.builder()
                .medicineId(1L)
                .stockQuantity(10)
                .build();

        Inventory saved = inventoryRepository.save(inventory);
        assertNotNull(saved.getId(), "After saving, the entity should have an ID");

        Optional<Inventory> found = inventoryRepository.findById(saved.getId());
        assertTrue(found.isPresent(), "Entity should be found");
        assertEquals(1L, found.get().getMedicineId());
        assertEquals(10, found.get().getStockQuantity());
    }

    @Test
    @DisplayName("Test updating an existing Inventory entity")
    void testUpdate() {
        Inventory inventory = Inventory.builder()
                .medicineId(1L)
                .stockQuantity(5)
                .build();

        Inventory saved = inventoryRepository.save(inventory);
        Long savedId = saved.getId();
        assertNotNull(savedId, "Saved entity must have a non-null ID");

        saved.setStockQuantity(20);
        inventoryRepository.save(saved);

        Optional<Inventory> updated = inventoryRepository.findById(savedId);
        assertTrue(updated.isPresent(), "Updated entity should still exist");
        assertEquals(20, updated.get().getStockQuantity());
    }

    @Test
    @DisplayName("Test deleting an Inventory entity")
    void testDelete() {
        Inventory inventory = Inventory.builder()
                .medicineId(1L)
                .stockQuantity(50)
                .build();

        Inventory saved = inventoryRepository.save(inventory);
        Long savedId = saved.getId();
        assertNotNull(savedId, "Entity must have an ID after saving");

        inventoryRepository.deleteById(savedId);

        Optional<Inventory> deleted = inventoryRepository.findById(savedId);
        assertFalse(deleted.isPresent(), "Entity should no longer be in the database");
    }
}