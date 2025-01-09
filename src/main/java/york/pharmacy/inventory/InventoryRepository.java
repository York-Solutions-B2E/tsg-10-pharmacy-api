package york.pharmacy.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByMedicineId(Long medicineId);

    @Transactional
    @Modifying
    @Query("UPDATE Inventory i SET i.stockQuantity = :stockQuantity WHERE i.id = :id")
    void setStockQuantity(Long id, int stockQuantity);
}
