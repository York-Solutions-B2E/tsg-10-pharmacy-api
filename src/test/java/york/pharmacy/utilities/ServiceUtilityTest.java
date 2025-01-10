package york.pharmacy.utilities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import york.pharmacy.inventory.Inventory;
import york.pharmacy.inventory.InventoryRepository;
import york.pharmacy.inventory.dto.InventoryResponse;
import york.pharmacy.kafka.KafkaProducer;
import york.pharmacy.kafka.ProducerEvent;
import york.pharmacy.medicines.Medicine;
import york.pharmacy.medicines.MedicineRepository;
import york.pharmacy.medicines.MedicineService;
import york.pharmacy.medicines.dto.MedicineRequest;
import york.pharmacy.orders.Order;
import york.pharmacy.orders.OrderRepository;
import york.pharmacy.orders.OrderStatus;
import york.pharmacy.prescriptions.Prescription;
import york.pharmacy.prescriptions.PrescriptionRepository;
import york.pharmacy.prescriptions.PrescriptionStatus;
import york.pharmacy.prescriptions.dto.PrescriptionRequest;
import york.pharmacy.prescriptions.dto.PrescriptionResponse;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceUtilityTest {

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private MedicineRepository medicineRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private KafkaProducer kafkaProducer;

    @InjectMocks
    private ServiceUtility underTest;

    private Medicine medicine;

    @BeforeEach
    void setUp() {
        medicine = new Medicine(1L, "Jelly Beans", "JBX-001", Instant.now(), Instant.now());

    }

    @Test
    void getMedicineByCode() {
        // Arrange
        when(medicineRepository.findMedicineByCode("JBX-001")).thenReturn(medicine);

        // Act
        Medicine result = underTest.getMedicineByCode("JBX-001");

        // Assert
        assertNotNull(result);
        assertEquals("Jelly Beans", result.getName());
        assertEquals("JBX-001", result.getCode());
        verify(medicineRepository, times(1)).findMedicineByCode("JBX-001");
    }

    @Test
    void fetchMedicineById() {
        // Arrange
        when(medicineRepository.findById(1L)).thenReturn(Optional.of(medicine));

        // Act
        Medicine result = underTest.fetchMedicineById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Jelly Beans", result.getName());
        assertEquals("JBX-001", result.getCode());
        verify(medicineRepository, times(1)).findById(1L);
    }

    @Test
    void adjustStockQuantity() {
        // Given
        long testId = 1L;
        int adjustment = 5;
        int testStockQuantity = 10;

        Inventory testInventory = Inventory.builder()
                .id(testId)
                .medicine(medicine)
                .stockQuantity(testStockQuantity)
                .build();

        Inventory adjustedInventory = Inventory.builder()
                .id(testId)
                .medicine(medicine)
                .stockQuantity(testStockQuantity + adjustment)
                .build();

        when(inventoryRepository.findById(testId))
                .thenReturn(Optional.of(testInventory));
        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(adjustedInventory);

        // When
        InventoryResponse result = underTest.adjustStockQuantity(testId, adjustment);

        // Then
        assertNotNull(result);
        assertEquals(testStockQuantity + adjustment, result.getStockQuantity());
        verify(inventoryRepository).findById(testId);
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void fetchInventoryById() {
        // Arrange
        long testId = 1L;
        int testStockQuantity = 10;

        Inventory testInventory = Inventory.builder()
                .id(testId)
                .medicine(medicine)
                .stockQuantity(testStockQuantity)
                .build();

        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(testInventory));

        // Act
        Inventory result = underTest.fetchInventoryById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Jelly Beans", result.getMedicine().getName());
        assertEquals("JBX-001", result.getMedicine().getCode());
        verify(inventoryRepository, times(1)).findById(1L);
    }

    @Test
    void getClosestOrderedDeliveryDate() {
        // Arrange
        Long inventoryId = 1L;
        LocalDate currentDate = LocalDate.now();

        Inventory inventory = new Inventory(1L, medicine, 500);

        Order expectedOrder = new Order(
                1L,
                inventory,
                100,
                LocalDate.of(2024, 12, 27),
                OrderStatus.ORDERED,
                Instant.now(),
                Instant.now()
        );

        when(orderRepository.findFirstOrderByInventoryIdAndStatusOrderedAndFutureDeliveryDate(currentDate, inventoryId))
                .thenReturn(Optional.of(expectedOrder));

        // Act
        Optional<Order> result = underTest.getClosestOrderedDeliveryDate(inventoryId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedOrder, result.get());
        verify(orderRepository, times(1))
                .findFirstOrderByInventoryIdAndStatusOrderedAndFutureDeliveryDate(currentDate, inventoryId);
    }

    @Test
    @Disabled
    void addPrescription() {
        Medicine medicine2 = new Medicine(1L, "Aspirin", "MED001", Instant.now(), Instant.now());

        PrescriptionRequest prescriptionRequest = new PrescriptionRequest(
                "1234L",
                "MED001",
                "111L",
                30,
                "Take after meals"
        );

        Prescription prescription = new Prescription(
                1L,
                "1234L",
                medicine2,
                "111L",
                30,
                "take after meals",
                PrescriptionStatus.NEW,
                null
        );

//        when(underTest.getMedicineByCode("MED001")).thenReturn(medicine2);
        when(prescriptionRepository.save(any(Prescription.class))).thenReturn(prescription);


        PrescriptionResponse response = underTest.addPrescription(prescriptionRequest);

        doNothing().when(kafkaProducer).sendMessage(anyString(), any(ProducerEvent.class));

        verify(kafkaProducer, times(1))
                .sendMessage(eq("prescription_status_updates"), any(ProducerEvent.class));

        assertNotNull(response);
        assertEquals(1, response.getId());
    }

    @Test
    void cancelPrescription() {
        Prescription prescription = new Prescription(
                1L,
                "1234L",
                medicine,
                "PID002",
                30,
                "take after meals",
                PrescriptionStatus.NEW,
                null
        );

        when(prescriptionRepository.findByPrescriptionNumber("PID002"))
                .thenReturn(Optional.of(prescription));
        when(prescriptionRepository.save(any(Prescription.class)))
                .thenReturn(prescription);

        underTest.cancelPrescription("PID002");

        assertEquals(PrescriptionStatus.CANCELLED, prescription.getStatus());
    }

    @Test
    void updateAwaitingShipmentStatus() {
        Prescription prescription = new Prescription(
                1L,
                "1234L",
                medicine,
                "PID002",
                30,
                "take after meals",
                PrescriptionStatus.NEW,
                null
        );
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

        doNothing().when(kafkaProducer).sendMessage(anyString(), any(ProducerEvent.class));

        List<Prescription> updatedPrescriptions = underTest.updateAwaitingShipmentStatus(order);



        assertEquals(1, updatedPrescriptions.size());
        for (Prescription p : updatedPrescriptions) {
            assertEquals(PrescriptionStatus.AWAITING_SHIPMENT, p.getStatus());
            assertEquals(order, p.getOrder());
        }

        verify(kafkaProducer, times(1))
                .sendMessage(eq("prescription_status_updates"), any(ProducerEvent.class));
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
                "1254L",
                medicine,
                "222L",
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
    void minOrderCount() {
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

    @Test
    void checkAndUpdatePrescriptionStock_returnsTrue() {
        Long medicineId = 1L;
        Long prescriptionId = 1L;

        List<PrescriptionStatus> statuses = List.of(
                PrescriptionStatus.NEW,
                PrescriptionStatus.OUT_OF_STOCK,
                PrescriptionStatus.STOCK_RECEIVED
        );

        when(prescriptionRepository.findTotalQuantityByMedicineIdAndStatus(medicineId, statuses))
                .thenReturn(50); // Total quantity needed

        Inventory inventory = new Inventory();
        inventory.setStockQuantity(100);

        when(inventoryRepository.findByMedicineId(medicineId))
                .thenReturn(Optional.of(inventory));

        // Act
        boolean result = underTest.checkAndUpdatePrescriptionStock(medicineId, prescriptionId);

        // Assert
        assertTrue(result);
    }

}