package york.pharmacy.prescriptions;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByMedicineId(Long medicineId);

    @Query("SELECT COALESCE(SUM(p.quantity), 0) " +
            "FROM Prescription p " +
            "WHERE p.medicine.id = :medicineId " +
            "AND (p.status = 'NEW' OR p.status = 'OUT_OF_STOCK')")
    int findTotalQuantityByMedicineIdAndStatus(Long medicineId);
}
