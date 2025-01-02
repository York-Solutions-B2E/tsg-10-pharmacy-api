package york.pharmacy.prescriptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import york.pharmacy.inventory.InventoryRepository;
import york.pharmacy.inventory.InventoryService;
import york.pharmacy.medicines.Medicine;
import york.pharmacy.medicines.MedicineRepository;
import york.pharmacy.medicines.MedicineService;
import york.pharmacy.prescriptions.dto.PrescriptionRequest;
import york.pharmacy.prescriptions.dto.PrescriptionResponse;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrescriptionServiceTest {

    @Mock
    PrescriptionRepository prescriptionRepository;

    @Mock
    MedicineService medicineService;

    @Mock
    InventoryService inventoryService;

    @InjectMocks
    PrescriptionService underTest;

    private Prescription prescription;
    private PrescriptionRequest prescriptionRequest;
    private Medicine medicine;

    @BeforeEach
    void setUp() {
//        underTest = new PrescriptionService(prescriptionRepository, medicineService, inventoryService);

        medicine = new Medicine(1L, "Aspirin", "MED001", Instant.now(), Instant.now());
        prescriptionRequest = new PrescriptionRequest(
                1L,
                "MED001",
                111L,
                30,
                "Take after meals"
        );

        prescription = new Prescription(
                1L,
                1234L,
                medicine,
                111L,
                30,
                "take after meals",
                PrescriptionStatus.NEW
        );
    }

    @Test
    void addPrescription() {
        when(medicineService.getMedicineByCode("MED001")).thenReturn(medicine);
        when(prescriptionRepository.save(any(Prescription.class))).thenReturn(prescription);

        PrescriptionResponse response = underTest.addPrescription(prescriptionRequest);

        assertNotNull(response);
        assertEquals(1, response.getId());
        verify(inventoryService, times(1)).updateSufficientStock(any());
    }

    @Test
    void getAllPrescriptions() {
        when(prescriptionRepository.findAll()).thenReturn(List.of(prescription));

        List<PrescriptionResponse> response = underTest.getAllPrescriptions();

        assertNotNull(response);
        assertEquals(1, response.size());
        verify(prescriptionRepository, times(1)).findAll();
    }

    @Test
    @Disabled
    void getPrescriptionById() {
        when(prescriptionRepository.findById(1L)).thenReturn(Optional.of(prescription));

        PrescriptionResponse response = underTest.getPrescriptionById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(prescriptionRepository, times(1)).findById(1L);
    }

    @Test
    @Disabled
    void updatePrescription() {
    }

    @Test
    @Disabled
    void cancelPrescription() {
    }

    @Test
    @Disabled
    void updateInventoryStockStatus() {
    }
}