package york.pharmacy.medicines;

import york.pharmacy.medicines.dto.MedicineRequest;
import york.pharmacy.medicines.dto.MedicineResponse;

public class MedicineMapper {

    // Convert MedicineRequest DTO to Medicine Entity
    public static Medicine toEntity(MedicineRequest medicineRequest) {
        return new Medicine(
                medicineRequest.getName(),
                medicineRequest.getCode()
        );
    }

    // Convert Medicine Entity to MedicineResponse DTO
    public static MedicineResponse toResponse(Medicine medicine) {
        return new MedicineResponse(
                medicine.getId(),
                medicine.getName(),
                medicine.getCode(),
                medicine.getCreatedAt(),
                medicine.getUpdatedAt()
        );
    }
}
