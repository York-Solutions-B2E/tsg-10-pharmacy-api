package york.pharmacy.prescriptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import york.pharmacy.inventory.Inventory;
import york.pharmacy.inventory.InventoryRepository; // <-- new import
import york.pharmacy.medicines.Medicine;
import york.pharmacy.medicines.MedicineService;
import york.pharmacy.orders.Order;
import york.pharmacy.orders.OrderStatus;
import york.pharmacy.prescriptions.dto.PrescriptionRequest;
import york.pharmacy.prescriptions.dto.PrescriptionResponse;
import york.pharmacy.prescriptions.dto.PrescriptionStatusRequest;

import java.time.Instant;
import java.time.LocalDate;
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

    // Remove InventoryService mock, since PrescriptionService no longer depends on InventoryService
    // @Mock
    // private InventoryService inventoryService;

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
                1234L,
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
                PrescriptionStatus.NEW,
                null
        );
    }

    @Test
    void addPrescription() {
        when(medicineService.getMedicineByCode("MED001"))
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

        // Suppose the inventory row has stockQuantity=50
        Inventory mockInventory = new Inventory(99L, medicine, 50);
        when(inventoryRepository.findByMedicineId(medicine.getId()))
                .thenReturn(Optional.of(mockInventory));

        when(prescriptionRepository.save(any(Prescription.class)))
                .thenReturn(prescription);

        // Execute
        PrescriptionResponse response = underTest.updatePrescription(1L, statusRequest);

        // Assert
        assertNotNull(response);
        assertEquals(PrescriptionStatus.FILLED, prescription.getStatus());

        // We also expect that the inventory's stock got decreased by 30 (the prescription quantity)
        // i.e. from 50 => 20
        assertEquals(20, mockInventory.getStockQuantity());
    }

    @Test
    void updatePrescriptionPickedUp_success() {
        // This prescription starts in FILLED => can go to PICKED_UP
        Prescription prescription2 = new Prescription(
                23L,
                1254L,
                medicine,
                222L,
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
                1254L,
                medicine,
                222L,
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

    @Test
    void updateAwaitingShipmentStatus() {
        Inventory inventory = new Inventory(1L, medicine, 500);
        Order order = new Order(
                123L,
                inventory,
                100,
                LocalDate.of(2025, 2, 27),
                OrderStatus.ORDERED,
                Instant.now(),
                Instant.now()
        );

        List<Prescription> mockPrescriptions = List.of(prescription);

        when(prescriptionRepository.findAllByMedicineIdAndStatus(
                1L,
                List.of(PrescriptionStatus.NEW, PrescriptionStatus.OUT_OF_STOCK))
        ).thenReturn(mockPrescriptions);

        when(prescriptionRepository.save(any(Prescription.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));  // Return the same object

        List<Prescription> updatedPrescriptions = underTest.updateAwaitingShipmentStatus(order);

        assertEquals(1, updatedPrescriptions.size());
        for (Prescription p : updatedPrescriptions) {
            assertEquals(PrescriptionStatus.AWAITING_SHIPMENT, p.getStatus());
            assertEquals(order, p.getOrder());
        }

        verify(prescriptionRepository, times(1)).save(any(Prescription.class));

        ArgumentCaptor<Prescription> captor = ArgumentCaptor.forClass(Prescription.class);
        verify(prescriptionRepository, times(1)).save(captor.capture());

        List<Prescription> savedPrescriptions = captor.getAllValues();
        assertEquals(1, savedPrescriptions.size());
        assertTrue(savedPrescriptions.contains(prescription));
    }

    @Test
    void updateStockReceivedStatus() {
        Inventory inventory = new Inventory(1L, medicine, 500);
        Order order = new Order(
                123L,
                inventory,
                100,
                LocalDate.of(2025, 2, 27),
                OrderStatus.RECEIVED,
                Instant.now(),
                Instant.now()
        );

        Prescription prescription2 = new Prescription(
                23L,
                1254L,
                medicine,
                222L,
                30,
                "take after meals",
                PrescriptionStatus.AWAITING_SHIPMENT,
                order
        );

        List<Prescription> mockPrescriptions = List.of(prescription2);

        when(prescriptionRepository.findAllByOrder(order))
                .thenReturn(mockPrescriptions);

        when(prescriptionRepository.save(any(Prescription.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));  // Return the same object

        List<Prescription> updatedPrescriptions = underTest.updateStockReceivedStatus(order);

        assertEquals(1, updatedPrescriptions.size());
        for (Prescription p : updatedPrescriptions) {
            assertEquals(PrescriptionStatus.STOCK_RECEIVED, p.getStatus());
            assertEquals(order, p.getOrder());
        }

        verify(prescriptionRepository, times(1)).save(any(Prescription.class));

        ArgumentCaptor<Prescription> captor = ArgumentCaptor.forClass(Prescription.class);
        verify(prescriptionRepository, times(1)).save(captor.capture());

        List<Prescription> savedPrescriptions = captor.getAllValues();
        assertEquals(1, savedPrescriptions.size());
        assertTrue(savedPrescriptions.contains(prescription2));
    }

    @Test
    void cancelPrescription() {
        when(prescriptionRepository.findById(1L))
                .thenReturn(Optional.of(prescription));
        when(prescriptionRepository.save(any(Prescription.class)))
                .thenReturn(prescription);

        underTest.cancelPrescription(1L);

        assertEquals(PrescriptionStatus.CANCELLED, prescription.getStatus());
    }

    @Test
    void minOrderCount_shouldReturnTotalCount() {
        Long medicineId = 1L;
        int expectedCount = 30;
        List<PrescriptionStatus> statuses = List.of(PrescriptionStatus.NEW, PrescriptionStatus.OUT_OF_STOCK);

        when(prescriptionRepository.findTotalQuantityByMedicineIdAndStatus(medicineId, statuses))
                .thenReturn(expectedCount);

        int result = underTest.minOrderCount(medicineId);

        assertEquals(expectedCount, result);
        verify(prescriptionRepository, times(1))
                .findTotalQuantityByMedicineIdAndStatus(medicineId, statuses);
    }

    /*
    @Test
    void updateInventoryStockStatus() {
        Long medicineId = 1L;
        int totalQuantity = 50;
        List<PrescriptionStatus> statuses = List.of(
                PrescriptionStatus.NEW,
                PrescriptionStatus.OUT_OF_STOCK,
                PrescriptionStatus.STOCK_RECEIVED
        );

        when(prescriptionRepository.findTotalQuantityByMedicineIdAndStatus(medicineId, statuses))
                .thenReturn(totalQuantity);

        // underTest.updateInventoryStockStatus(medicineId);

        HashMap<Long, Integer> expectedMap = new HashMap<>();
        expectedMap.put(medicineId, totalQuantity);

        verify(prescriptionRepository, times(1))
                .findTotalQuantityByMedicineIdAndStatus(medicineId, statuses);
    }
    */
}
