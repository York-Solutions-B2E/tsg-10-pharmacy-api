package york.pharmacy.medicines;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import york.pharmacy.medicines.dto.MedicineRequest;
import york.pharmacy.medicines.dto.MedicineResponse;
import york.pharmacy.exceptions.ResourceNotFoundException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicineServiceTest {

    @Mock
    private MedicineRepository medicineRepository;

    @InjectMocks
    private MedicineService medicineService;

    private Medicine medicine;
    private MedicineRequest request;

    @BeforeEach
    void setUp() {
        medicine = new Medicine(1L, "Aspirin", "MED001", Instant.now(), Instant.now());
        request = new MedicineRequest("Aspirin", "MED001");
    }

    @Test
    void testCreateMedicine_Success() {
        // Arrange
        when(medicineRepository.save(any(Medicine.class))).thenReturn(medicine);

        // Act
        MedicineResponse response = medicineService.createMedicine(request);

        // Assert
        assertNotNull(response);
        assertEquals("Aspirin", response.getName());
        verify(medicineRepository, times(1)).save(any(Medicine.class));
    }

    @Test
    void testBatchCreateMedicines_Success() {
        // Arrange
        List<MedicineRequest> requests = List.of(request, request);
        List<Medicine> medicines = List.of(medicine, medicine);
        when(medicineRepository.saveAll(anyList())).thenReturn(medicines);

        // Act
        List<MedicineResponse> responses = medicineService.batchCreateMedicines(requests);

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Aspirin", responses.get(0).getName());
        verify(medicineRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testGetAllMedicines_Success() {
        // Arrange
        when(medicineRepository.findAll()).thenReturn(List.of(medicine));

        // Act
        List<MedicineResponse> responses = medicineService.getAllMedicines();

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(medicineRepository, times(1)).findAll();
    }

    @Test
    void testGetMedicineById_Success() {
        // Arrange
        when(medicineRepository.findById(1L)).thenReturn(Optional.of(medicine));

        // Act
        MedicineResponse response = medicineService.getMedicineById(1L);

        // Assert
        assertNotNull(response);
        assertEquals("Aspirin", response.getName());
        verify(medicineRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateMedicine_Success() {
        // Arrange
        when(medicineRepository.findById(1L)).thenReturn(Optional.of(medicine));
        when(medicineRepository.save(any(Medicine.class))).thenReturn(medicine);

        // Act
        MedicineResponse response = medicineService.updateMedicine(1L, request);

        // Assert
        assertNotNull(response);
        assertEquals("Aspirin", response.getName());
        verify(medicineRepository, times(1)).findById(1L);
        verify(medicineRepository, times(1)).save(any(Medicine.class));
    }

    @Test
    void testDeleteMedicine_Success() {
        // Arrange
        when(medicineRepository.existsById(1L)).thenReturn(true);

        // Act
        assertDoesNotThrow(() -> medicineService.deleteMedicine(1L));

        // Assert
        verify(medicineRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteMedicine_NotFound() {
        // Arrange
        when(medicineRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> medicineService.deleteMedicine(1L));
        verify(medicineRepository, times(1)).existsById(1L);
    }

    @Test
    void testGetMedicineByCode_Success() {
        // Arrange
        when(medicineRepository.findMedicineByCode("MED001")).thenReturn(medicine);

        // Act
        Medicine result = medicineService.getMedicineByCode("MED001");

        // Assert
        assertNotNull(result);
        assertEquals("Aspirin", result.getName());
        assertEquals("MED001", result.getCode());
        verify(medicineRepository, times(1)).findMedicineByCode("MED001");
    }

    @Test
    void testFetchMedicineById_Success() {
        // Arrange
        when(medicineRepository.findById(1L)).thenReturn(Optional.of(medicine));

        // Act
        Medicine result = medicineService.fetchMedicineById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Aspirin", result.getName());
        assertEquals("MED001", result.getCode());
        verify(medicineRepository, times(1)).findById(1L);
    }

}
