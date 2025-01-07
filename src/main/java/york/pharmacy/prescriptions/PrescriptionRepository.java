package york.pharmacy.prescriptions;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import york.pharmacy.orders.Order;
import york.pharmacy.prescriptions.dto.PrescriptionResponse;

import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByMedicineId(Long medicineId);

    @Query("SELECT COALESCE(SUM(p.quantity), 0) " +
            "FROM Prescription p " +
            "WHERE p.medicine.id = :medicineId " +
            "AND p.status IN (:statuses)")
    int findTotalQuantityByMedicineIdAndStatus(Long medicineId, List<PrescriptionStatus> statuses);

    @Query("SELECT p FROM Prescription p WHERE p.status NOT IN (:excludedStatuses)")
    List<Prescription> findAllByStatusExcept(@Param("excludedStatuses") List<PrescriptionStatus> excludedStatuses);

    @Query("SELECT p FROM Prescription p WHERE p.medicine.id = :medicineId AND p.status IN (:statuses)")
    List<Prescription> findAllByMedicineIdAndStatus(Long medicineId, List<PrescriptionStatus> statuses);

    List<Prescription> findAllByOrder(Order order);
}
