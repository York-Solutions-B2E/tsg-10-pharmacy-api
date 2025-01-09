package york.pharmacy.prescriptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import york.pharmacy.medicines.Medicine;
import york.pharmacy.prescriptions.dto.PrescriptionRequest;
import york.pharmacy.prescriptions.dto.PrescriptionResponse;
import york.pharmacy.prescriptions.dto.PrescriptionStatusRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrescriptionControllerTest {

    @Mock
    private PrescriptionService prescriptionService;

    @InjectMocks
    private PrescriptionController underTest;

    private PrescriptionRequest prescriptionRequest;
    private PrescriptionResponse prescriptionResponse;
    private Medicine medicine;

    @BeforeEach
    void setUp() {

        prescriptionRequest = new PrescriptionRequest(
                "1L",
                "XOF03",
                "555L",
                30,
                "Take after meals"
        );
        medicine = new Medicine();
        medicine.setId(1L);
        medicine.setCode("XOF03");
        medicine.setName("Jelly Beans");
        prescriptionResponse = new PrescriptionResponse(
                1L,
                "123L",
                medicine,
                "555L",
                30,
                "Take after Meals",
                PrescriptionStatus.NEW
        );

    }

    @Test
    void testCreatePrescription() {
        when(prescriptionService.addPrescription(any(PrescriptionRequest.class))).thenReturn(prescriptionResponse);

        ResponseEntity<PrescriptionResponse> response = underTest.createPrescription(prescriptionRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(PrescriptionStatus.NEW, response.getBody().getStatus());
        verify(prescriptionService, times(1)).addPrescription(any());
    }

    @Test
    void getAllPrescriptions() {
        when(prescriptionService.getAllPrescriptions()).thenReturn(List.of(prescriptionResponse));

        ResponseEntity<List<PrescriptionResponse>> response = underTest.getAllPrescriptions();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(prescriptionResponse), response.getBody());
        verify(prescriptionService, times(1)).getAllPrescriptions();

    }

    @Test
    void getActivePrescriptions() {
        when(prescriptionService.getActivePrescriptions()).thenReturn(List.of(prescriptionResponse));

        ResponseEntity<List<PrescriptionResponse>> response = underTest.getAllActivePrescriptions();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(prescriptionResponse), response.getBody());
        verify(prescriptionService, times(1)).getActivePrescriptions();

    }

    @Test
    void getPrescriptionById() {
        when(prescriptionService.getPrescriptionById(1L)).thenReturn(prescriptionResponse);

        ResponseEntity<PrescriptionResponse> response = underTest.getPrescriptionById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(prescriptionResponse, response.getBody());
        verify(prescriptionService, times(1)).getPrescriptionById(1L);
    }

    @Test
    void updatePrescription() {
        PrescriptionResponse updatedResponse = new PrescriptionResponse(
                1L,
                "123L",
                medicine,
                "555L",
                30,
                "Take after Meals",
                PrescriptionStatus.FILLED
        );
        PrescriptionStatusRequest updatedStatusRequest = new PrescriptionStatusRequest(PrescriptionStatus.FILLED);

        when(prescriptionService.updatePrescription(1L, updatedStatusRequest)).thenReturn(updatedResponse);

        ResponseEntity<PrescriptionResponse> response = underTest.updatePrescription(1L, updatedStatusRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(prescriptionService, times(1)).updatePrescription(1L, updatedStatusRequest);
    }
}