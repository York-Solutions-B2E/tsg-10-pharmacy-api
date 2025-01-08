package york.pharmacy.prescriptions;

public interface PrescriptionServiceAdapter {
    /**
     * Return how many pills are needed for the given medicineId.
     * This is a minimal contract that InventoryService needs.
     */
    int minOrderCount(Long medicineId);
}
