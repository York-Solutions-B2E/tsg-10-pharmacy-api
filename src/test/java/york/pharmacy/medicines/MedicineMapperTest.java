package york.pharmacy.medicine;

import org.junit.jupiter.api.Test;
import york.pharmacy.medicines.Medicine;
import york.pharmacy.medicines.MedicineMapper;
import york.pharmacy.medicines.dto.MedicineRequest;
import york.pharmacy.medicines.dto.MedicineResponse;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class MedicineMapperTest {

    @Test
    void testToEntity() {
        // Arrange
        MedicineRequest request = new MedicineRequest("Paracetamol", "MED123");

        // Act
        Medicine entity = MedicineMapper.toEntity(request);

        // Assert
        assertNotNull(entity);
        assertEquals("Paracetamol", entity.getName());
        assertEquals("MED123", entity.getCode());
    }

    @Test
    void testToResponse() {
        // Arrange
        Medicine entity = new Medicine(1L, "Ibuprofen", "MED456", Instant.now(), Instant.now());

        // Act
        MedicineResponse response = MedicineMapper.toResponse(entity);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Ibuprofen", response.getName());
        assertEquals("MED456", response.getCode());
    }

}
