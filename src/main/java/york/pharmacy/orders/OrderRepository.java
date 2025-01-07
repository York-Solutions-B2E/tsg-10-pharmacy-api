package york.pharmacy.orders;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.status = 'ORDERED' AND o.deliveryDate > :currentDate AND o.inventory.id = :inventoryId ORDER BY o.deliveryDate ASC LIMIT 1")
    Optional<Order> findFirstOrderByInventoryIdAndStatusOrderedAndFutureDeliveryDate(
            @Param("currentDate") LocalDate currentDate,
            @Param("inventoryId") Long medicineId);

}
