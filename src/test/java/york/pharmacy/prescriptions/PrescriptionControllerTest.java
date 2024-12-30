package york.pharmacy.prescriptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import york.pharmacy.prescriptions.dto.PrescriptionRequest;
import york.pharmacy.prescriptions.dto.PrescriptionResponse;

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

    @BeforeEach
    void setUp() {

        prescriptionRequest = new PrescriptionRequest(
                1L,
                101L,
                555L,
                3,
                "Take after meals"
        );

        prescriptionResponse = new PrescriptionResponse(
                1L,
                101L,
                555L,
                3,
                "Take after meals",
                PrescriptionStatus.NEW
        );
    }

    @Test
    void testCreatePrescription() {
        when(prescriptionService.addPrescription(any(PrescriptionRequest.class))).thenReturn(prescriptionResponse);

        ResponseEntity<PrescriptionResponse> response = underTest.createPrescription(prescriptionRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertEquals(PrescriptionStatus.NEW, response.getBody().getStatus());
//        verify(prescriptionService, times(1)).addPrescription(any());
    }

    @Test
    void getAllPrescriptions() {
    }

    @Test
    void getPrescriptionById() {
    }

    @Test
    void updatePrescription() {
    }
}