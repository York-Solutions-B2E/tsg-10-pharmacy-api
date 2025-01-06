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
    Optional<List<Order>> findByMedicineIdAndStatus(Long medicineId, OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.status = 'ORDERED' AND o.deliveryDate > :currentDate AND o.medicine.id = :medicineId ORDER BY o.deliveryDate ASC")
    Optional<Order> findFirstByMedicineIdAndStatusOrderedAndFutureDeliveryDate(
            @Param("currentDate") LocalDate currentDate,
            @Param("medicineId") Long medicineId);

}
