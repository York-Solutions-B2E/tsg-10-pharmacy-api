package york.pharmacy.prescriptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import york.pharmacy.inventory.InventoryRepository;
import york.pharmacy.medicines.Medicine;
import york.pharmacy.medicines.MedicineService;
import york.pharmacy.prescriptions.dto.PrescriptionRequest;
import york.pharmacy.prescriptions.dto.PrescriptionResponse;
import york.pharmacy.prescriptions.dto.PrescriptionStatusRequest;
import york.pharmacy.utilities.ServiceUtility;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrescriptionServiceTest {

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private MedicineService medicineService;

     @Mock
     private ServiceUtility serviceUtility;

    @Mock
    private InventoryRepository inventoryRepository;

    // This injects the above mocks into the PrescriptionService constructor
    @InjectMocks
    private PrescriptionService underTest;

    private Prescription prescription;
    private PrescriptionRequest prescriptionRequest;
    private Medicine medicine;

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
    void addPrescription() {
        when(serviceUtility.getMedicineByCode("MED001"))
                .thenReturn(medicine);
        when(prescriptionRepository.save(any(Prescription.class)))
                .thenReturn(prescription);

        PrescriptionResponse response = underTest.addPrescription(prescriptionRequest);

        assertNotNull(response);
        assertEquals(1, response.getId());

    }

    @Test
    void getAllPrescriptions() {
        when(prescriptionRepository.findAll())
                .thenReturn(List.of(prescription));

        List<PrescriptionResponse> response = underTest.getAllPrescriptions();

        assertNotNull(response);
        assertEquals(1, response.size());
        verify(prescriptionRepository, times(1)).findAll();
    }

    @Test
    void getActivePrescriptions() {
        when(prescriptionRepository.findAllByStatusExcept(
                List.of(PrescriptionStatus.CANCELLED, PrescriptionStatus.PICKED_UP))
        ).thenReturn(List.of(prescription));

        List<PrescriptionResponse> response = underTest.getActivePrescriptions();

        assertNotNull(response);
        assertEquals(1, response.size());
        verify(prescriptionRepository, times(1))
                .findAllByStatusExcept(List.of(PrescriptionStatus.CANCELLED, PrescriptionStatus.PICKED_UP));
    }

    @Test
    void getPrescriptionById() {
        when(prescriptionRepository.findById(1L))
                .thenReturn(Optional.of(prescription));

        PrescriptionResponse response = underTest.getPrescriptionById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(prescriptionRepository, times(1)).findById(1L);
    }

    @Test
    void updatePrescriptionFilled_success() {
        // Prepare a request that sets status to FILLED
        PrescriptionStatusRequest statusRequest = new PrescriptionStatusRequest(PrescriptionStatus.FILLED);

        // The existing prescription is in status NEW => valid transition to FILLED
        when(prescriptionRepository.findById(1L))
                .thenReturn(Optional.of(prescription));


        when(prescriptionRepository.save(any(Prescription.class)))
                .thenReturn(prescription);

        // Execute
        PrescriptionResponse response = underTest.updatePrescription(1L, statusRequest);

        // Assert
        assertNotNull(response);
        assertEquals(PrescriptionStatus.FILLED, prescription.getStatus());
        verify(serviceUtility, times(1)).adjustStockQuantity(eq(1L), eq(-30)); // Verify stock update
        verify(serviceUtility, times(1)).publishPickedUpOrFilled(eq("FILLED"), eq("111L")); // Verify notification
        verify(prescriptionRepository, times(1)).save(any(Prescription.class)); // Verify save
    }

    @Test
    void updatePrescriptionPickedUp_success() {
        // This prescription starts in FILLED => can go to PICKED_UP
        Prescription prescription2 = new Prescription(
                23L,
                "1254L",
                medicine,
                "222L",
                30,
                "take after meals",
                PrescriptionStatus.FILLED,
                null
        );
        PrescriptionStatusRequest statusRequest = new PrescriptionStatusRequest(PrescriptionStatus.PICKED_UP);

        when(prescriptionRepository.findById(23L))
                .thenReturn(Optional.of(prescription2));
        when(prescriptionRepository.save(any(Prescription.class)))
                .thenReturn(prescription2);

        PrescriptionResponse response = underTest.updatePrescription(23L, statusRequest);

        assertNotNull(response);
        assertEquals(PrescriptionStatus.PICKED_UP, prescription2.getStatus());
    }

    @Test
    void updatePrescriptionFilled_failure() {
        // If the prescription is CANCELLED, it cannot go to FILLED
        Prescription prescription2 = new Prescription(
                23L,
                "1254L",
                medicine,
                "222L",
                30,
                "take after meals",
                PrescriptionStatus.CANCELLED,
                null
        );
        PrescriptionStatusRequest statusRequest = new PrescriptionStatusRequest(PrescriptionStatus.FILLED);

        when(prescriptionRepository.findById(23L))
                .thenReturn(Optional.of(prescription2));

        IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> underTest.updatePrescription(23L, statusRequest)
        );

        assertEquals(
                "Prescription cannot be marked as FILLED from the current state: CANCELLED",
                thrown.getMessage()
        );
    }

    @Test
    void updatePrescriptionPickedUp_failure() {
        // If the prescription is in NEW, it cannot go directly to PICKED_UP
        PrescriptionStatusRequest statusRequest = new PrescriptionStatusRequest(PrescriptionStatus.PICKED_UP);

        when(prescriptionRepository.findById(1L))
                .thenReturn(Optional.of(prescription));

        IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> underTest.updatePrescription(1L, statusRequest)
        );

        assertEquals(
                "Prescription cannot be marked as PICKED_UP from the current state: NEW",
                thrown.getMessage()
        );
    }
}
