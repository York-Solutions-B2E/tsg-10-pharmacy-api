package york.pharmacy.prescriptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import york.pharmacy.medicines.Medicine;
import york.pharmacy.prescriptions.dto.PrescriptionRequest;
import york.pharmacy.prescriptions.dto.PrescriptionResponse;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class PrescriptionMapperTest {

    private Medicine medicine;
    private Prescription prescription;
    private PrescriptionRequest prescriptionRequest;

    @BeforeEach
    void setUp() {
        medicine = new Medicine(1L, "Aspirin", "MED001", Instant.now(), Instant.now());
        prescriptionRequest = new PrescriptionRequest(
                "1234L",
                "MED001",
                "111L",
                30,
                "Take after meals"
        );

        prescription = new Prescription(
                1L,
                "1234L",
                medicine,
                "111L",
                30,
                "take after meals",
                PrescriptionStatus.NEW,
                null
        );
    }

    @Test
    void toEntity() {
        Prescription mappedPrescription = PrescriptionMapper.toEntity(prescriptionRequest, medicine);

        assertNotNull(mappedPrescription);
        assertEquals(medicine, mappedPrescription.getMedicine());
        assertEquals(PrescriptionStatus.NEW, mappedPrescription.getStatus());
        assertEquals("1234L", mappedPrescription.getPatientId());
    }

    @Test
    void toResponse() {
        PrescriptionResponse response = PrescriptionMapper.toResponse(prescription);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(medicine, response.getMedicine());
        assertEquals(PrescriptionStatus.NEW, response.getStatus());
        assertEquals("1234L", response.getPatientId());
        assertEquals(30, response.getQuantity());
        assertEquals("take after meals", response.getInstructions());
        assertEquals("111L", response.getPrescriptionNumber());
    }
}