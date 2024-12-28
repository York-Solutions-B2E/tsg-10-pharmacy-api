package york.pharmacy.medInventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for MedInventory entities.
 */
@Repository
public interface MedInventoryRepository extends JpaRepository<MedInventoryEntity, Long> {
    // No extra methods needed for basic CRUD operations.
    // You can define custom queries here if required.
}
