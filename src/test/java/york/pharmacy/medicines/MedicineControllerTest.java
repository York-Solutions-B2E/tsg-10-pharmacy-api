package york.pharmacy.medicines;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import york.pharmacy.medicines.dto.MedicineRequest;
import york.pharmacy.medicines.dto.MedicineResponse;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicineControllerTest {

    @Mock
    private MedicineService medicineService;

    @InjectMocks
    private MedicineController medicineController;

    private MedicineRequest request;
    private MedicineResponse response;

    @BeforeEach
    void setUp() {
        request = new MedicineRequest("Ibuprofen", "MED123");
        response = new MedicineResponse(1L, "Ibuprofen", "MED123", Instant.now(), Instant.now());
    }

    @Test
    void testCreateMedicine_Success() {
        // Arrange
        when(medicineService.createMedicine(any(MedicineRequest.class))).thenReturn(response);

        // Act
        ResponseEntity<MedicineResponse> result = medicineController.createMedicine(request);

        // Assert
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("Ibuprofen", result.getBody().getName());
        verify(medicineService, times(1)).createMedicine(any(MedicineRequest.class));
    }

    @Test
    void testBatchCreateMedicines_Success() {
        // Arrange
        List<MedicineRequest> requests = List.of(request);
        List<MedicineResponse> responses = List.of(response);
        when(medicineService.batchCreateMedicines(anyList())).thenReturn(responses);

        // Act
        ResponseEntity<List<MedicineResponse>> result = medicineController.batchCreateMedicines(requests);

        // Assert
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        verify(medicineService, times(1)).batchCreateMedicines(anyList());
    }

    @Test
    void testGetAllMedicines_Success() {
        // Arrange
        List<MedicineResponse> responses = List.of(response);
        when(medicineService.getAllMedicines()).thenReturn(responses);

        // Act
        ResponseEntity<List<MedicineResponse>> result = medicineController.getAllMedicines();

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        verify(medicineService, times(1)).getAllMedicines();
    }

    @Test
    void testGetMedicineById_Success() {
        // Arrange
        when(medicineService.getMedicineById(1L)).thenReturn(response);

        // Act
        ResponseEntity<MedicineResponse> result = medicineController.getMedicineById(1L);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Ibuprofen", result.getBody().getName());
        verify(medicineService, times(1)).getMedicineById(1L);
    }

    @Test
    void testUpdateMedicine_Success() {
        // Arrange
        when(medicineService.updateMedicine(eq(1L), any(MedicineRequest.class))).thenReturn(response);

        // Act
        ResponseEntity<MedicineResponse> result = medicineController.updateMedicine(1L, request);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Ibuprofen", result.getBody().getName());
        verify(medicineService, times(1)).updateMedicine(eq(1L), any(MedicineRequest.class));
    }

    @Test
    void testDeleteMedicine_Success() {
        // Arrange
        doNothing().when(medicineService).deleteMedicine(1L);

        // Act
        ResponseEntity<Void> result = medicineController.deleteMedicine(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(medicineService, times(1)).deleteMedicine(1L);
    }
}
