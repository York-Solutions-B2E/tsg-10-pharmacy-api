package york.pharmacy.inventory;

import org.springframework.dao.DataIntegrityViolationException;
import york.pharmacy.inventory.dto.InventoryRequest;
import york.pharmacy.inventory.dto.InventoryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import york.pharmacy.medicines.Medicine;
import york.pharmacy.medicines.MedicineService;
import york.pharmacy.orders.OrderService;
import york.pharmacy.prescriptions.PrescriptionService;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {
    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private MedicineService medicineService;

    @Mock
    private OrderService orderService;

    @Mock
    private PrescriptionService prescriptionService;

    @InjectMocks
    private InventoryService inventoryService;

    // Common test data
    private Long testId;
    private Long testMedicineId;
    private Medicine medicine;
    private int testStockQuantity;
    private Inventory testInventory;
    private InventoryRequest testRequest;
    private InventoryResponse expectedResponse;

    @BeforeEach
    void setUp() {
        // Initialize test data
        testId = 1L;
        testMedicineId = 2L;
        medicine = new Medicine(2L, "Jelly Beans", "J-01", Instant.now(), Instant.now());
        testStockQuantity = 10;

        testInventory = Inventory.builder()
                .id(testId)
                .medicine(medicine)
                .stockQuantity(testStockQuantity)
                .build();

        testRequest = InventoryRequest.builder()
                .medicineId(testMedicineId)
                .stockQuantity(testStockQuantity)
                .build();

        expectedResponse = InventoryResponse.builder()
                .id(testId)
                .medicine(medicine)
                .stockQuantity(testStockQuantity)
                .build();
    }

    @Test
    @DisplayName("Should create a new Inventory successfully")
    void testCreateInventory() {
        // Given
        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(testInventory);

        // When
        InventoryResponse result = inventoryService.createInventory(testRequest);

        // Then
        assertNotNull(result);
        assertEquals(testId, result.getId());
        assertEquals(testMedicineId, result.getMedicine().getId());
        assertEquals(testStockQuantity, result.getStockQuantity());
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    @DisplayName("Should create multiple Inventories successfully")
    void testCreateManyInventories() {
        // Given
        InventoryRequest secondRequest = InventoryRequest.builder()
                .medicineId(2L)
                .stockQuantity(20)
                .build();
        List<InventoryRequest> requests = Arrays.asList(testRequest, secondRequest);

        Inventory secondInventory = Inventory.builder()
                .id(2L)
                .medicine(medicine)
                .stockQuantity(20)
                .build();
        List<Inventory> savedEntities = Arrays.asList(testInventory, secondInventory);

        when(inventoryRepository.saveAll(any())).thenReturn(savedEntities);

        // When
        List<InventoryResponse> results = inventoryService.createManyInventories(requests);

        // Then
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(testMedicineId, results.get(0).getMedicine().getId());
        assertEquals(2L, results.get(1).getMedicine().getId());
        verify(inventoryRepository, times(1)).saveAll(any());
    }

    @Test
    @DisplayName("Should get an existing Inventory by Id")
    void testGetInventoryById() {
        // Given
        when(inventoryRepository.findById(testId))
                .thenReturn(Optional.of(testInventory));
        when(prescriptionService.minOrderCount(testMedicineId)).thenReturn(25);

        // When
        InventoryResponse result = inventoryService.getInventoryById(testId);

        // Then
        assertNotNull(result);
        assertEquals(testMedicineId, result.getMedicine().getId());
        verify(inventoryRepository, times(1)).findById(testId);
    }

    @Test
    @DisplayName("Should retrieve all Inventory entries")
    void testGetAllInventories() {
        // Given
        Inventory secondInventory = Inventory.builder()
                .id(2L)
                .medicine(medicine)
                .stockQuantity(20)
                .build();

        when(inventoryRepository.findAll())
                .thenReturn(Arrays.asList(testInventory, secondInventory));

        // When
        var results = inventoryService.getAllInventories();

        // Then
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(testMedicineId, results.get(0).getMedicine().getId());
        assertEquals(2L, results.get(1).getMedicine().getId());
        verify(inventoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should update an existing Inventory successfully while ignoring changes to medicineId")
    void testUpdateInventory() {
        // Given
        Long attemptedNewMedicineId = 999L;
        InventoryRequest updateRequest = InventoryRequest.builder()
                .medicineId(attemptedNewMedicineId) // Attempt to change medicineId; should be ignored
                .stockQuantity(20)                  // Actually update the stock quantity
                .build();

        // The inventory we find in the DB
        Inventory originalInventory = Inventory.builder()
                .id(testId)
                .medicine(medicine)     // testMedicineId = 2L from setUp()
                .stockQuantity(testStockQuantity) // e.g. 10
                .sufficientStock(false) // remains false unless the service changes it
                .build();

        // The inventory after update: only stockQuantity changes from 10 -> 20
        // (sufficientStock remains false because the service doesn't override it)
        Inventory updatedInventory = Inventory.builder()
                .id(testId)
                .medicine(medicine)
                .stockQuantity(20)
                .sufficientStock(false)
                .build();

        when(inventoryRepository.findById(testId))
                .thenReturn(Optional.of(originalInventory));
        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(updatedInventory);

        // When
        InventoryResponse result = inventoryService.updateInventory(testId, updateRequest);

        // Then
        assertNotNull(result, "Returned InventoryResponse should not be null");
        assertEquals(testId, result.getId(), "Inventory ID should remain the same");
        assertEquals(testMedicineId, result.getMedicine().getId(),
                "MedicineId should remain the original (2L) despite the update attempt");
        assertEquals(20, result.getStockQuantity(),
                "Stock quantity should be updated to 20");
        // Because the service code doesn't change sufficientStock,
        // we expect it to remain false (same as the original).
        assertFalse(result.getSufficientStock(),
                "Expected sufficientStock to remain false if service doesn't override it");

        // Verify the 'save' call was made with the correct final entity
        verify(inventoryRepository).findById(testId);
        verify(inventoryRepository).save(argThat(savedInventory ->
                savedInventory.getId().equals(testId)
                        && savedInventory.getMedicine().getId().equals(testMedicineId)
                        && savedInventory.getStockQuantity() == 20
                        && Boolean.FALSE.equals(savedInventory.getSufficientStock())
        ));
    }

    @Test
    @DisplayName("Should delete an existing Inventory by Id")
    void testDeleteInventory() {
        // Given
        when(inventoryRepository.existsById(testId)).thenReturn(true);

        // When
        inventoryService.deleteInventory(testId);

        // Then
        verify(inventoryRepository).existsById(testId);
        verify(inventoryRepository).deleteById(testId);
    }

    @Test
    @DisplayName("Should throw exception when deleting a non-existing Inventory")
    void testDeleteInventoryNotFound() {
        // Given
        Long nonExistingId = 999L;
        when(inventoryRepository.existsById(nonExistingId)).thenReturn(false);

        // When/Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> inventoryService.deleteInventory(nonExistingId));

        assertEquals("Inventory not found with id: " + nonExistingId, ex.getMessage());
        verify(inventoryRepository).existsById(nonExistingId);
        verify(inventoryRepository, never()).deleteById(nonExistingId);
    }

    @Test
    @DisplayName("Should keep sufficientStock = true if current stock >= needed")
    void testUpdateSufficientStockRemainsTrue() {

        int requiredAmount = 5; // Less than the 10 in Inventory
        HashMap<Long, Integer> medicineCount = new HashMap<>();
        medicineCount.put(testMedicineId, requiredAmount);

        // Already true by default
        Inventory inventoryInDb = Inventory.builder()
                .id(testId)
                .medicine(medicine)
                .stockQuantity(testStockQuantity) // 10
                .sufficientStock(true)            // default
                .build();

        // Service sees stock is sufficient => stays true
        when(inventoryRepository.findByMedicineId(testMedicineId))
                .thenReturn(Optional.of(inventoryInDb));
        when(inventoryRepository.save(any(Inventory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, Inventory.class));
        // or .thenReturn(inventoryInDb) if you like

        // When
        InventoryResponse result = inventoryService.updateSufficientStock(medicineCount);

        // Then
        assertNotNull(result);
        assertTrue(result.getSufficientStock(),
                "Expected sufficientStock to remain true when stock >= needed");
    }


    @Test
    @DisplayName("Should update sufficient stock status to false if current stock is less than needed")
    void testUpdateSufficientStockBecomesFalse() {

        int requiredAmount = testStockQuantity + 5; // 15 > 10, to test "sufficientStock"
        HashMap<Long, Integer> medicineCount = new HashMap<>();
        medicineCount.put(testMedicineId, requiredAmount);

        Inventory inventoryInDb = Inventory.builder()
                .id(testId)
                .medicine(medicine)
                .stockQuantity(testStockQuantity)  // 10
                .sufficientStock(true)             // default
                .build();

        // Service "sees" stock is insufficient and sets to false
        Inventory inventoryWithInsufficientStock = Inventory.builder()
                .id(testId)
                .medicine(medicine)
                .stockQuantity(testStockQuantity)  // still 10
                .sufficientStock(false)            // forced to false
                .build();

        when(inventoryRepository.findByMedicineId(testMedicineId))
                .thenReturn(Optional.of(inventoryInDb));
        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(inventoryWithInsufficientStock);

        // When
        InventoryResponse result = inventoryService.updateSufficientStock(medicineCount);

        // Then
        assertNotNull(result);
        assertFalse(result.getSufficientStock(),
                "Expected sufficientStock to be false when stock < needed");
        verify(inventoryRepository).findByMedicineId(testMedicineId);
        verify(inventoryRepository).save(any(Inventory.class));
    }


    @Test
    @DisplayName("Should adjust stock quantity successfully for a positive adjustment")
    void testAdjustStockQuantityPositive() {
        // Given
        int adjustment = 5;
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
        InventoryResponse result = inventoryService.adjustStockQuantity(testId, adjustment);

        // Then
        assertNotNull(result);
        assertEquals(testStockQuantity + adjustment, result.getStockQuantity());
        verify(inventoryRepository).findById(testId);
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    @DisplayName("Should adjust stock quantity successfully for a negative adjustment")
    void testAdjustStockQuantityNegative() {
        // Given
        int adjustment = -3;
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
        InventoryResponse result = inventoryService.adjustStockQuantity(testId, adjustment);

        // Then
        assertNotNull(result);
        assertEquals(testStockQuantity + adjustment, result.getStockQuantity());
        verify(inventoryRepository).findById(testId);
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    @DisplayName("Should throw exception when adjusting stock quantity below zero")
    void testAdjustStockQuantityBelowZero() {
        // Given
        int adjustment = -15; // More than current stock
        when(inventoryRepository.findById(testId))
                .thenReturn(Optional.of(testInventory));

        // When/Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> inventoryService.adjustStockQuantity(testId, adjustment));

        assertEquals("Cannot reduce stock below 0", ex.getMessage());
        verify(inventoryRepository).findById(testId);
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    @DisplayName("Should throw DataIntegrityViolationException if duplicate medicineId is inserted (single create)")
    void testCreateInventoryDuplicateMedicineId() {
        // Given
        // We simulate the DB or service constraint throwing DataIntegrityViolationException
        doThrow(new DataIntegrityViolationException("Duplicate key"))
                .when(inventoryRepository).save(any(Inventory.class));

        // When/Then
        assertThrows(DataIntegrityViolationException.class,
                () -> inventoryService.createInventory(testRequest));

        // The repository's save(...) was called once and threw
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    @DisplayName("Should throw DataIntegrityViolationException if duplicate medicineId is inserted in bulk create")
    void testCreateManyInventoriesDuplicateMedicineId() {
        // Given
        // Let's reuse your existing testRequest plus a second request
        InventoryRequest secondRequest = InventoryRequest.builder()
                .medicineId(2L)     // Same medicineId as testRequest
                .stockQuantity(20)
                .build();
        List<InventoryRequest> requests = Arrays.asList(testRequest, secondRequest);

        // We simulate the DB or service constraint throwing DataIntegrityViolationException
        doThrow(new DataIntegrityViolationException("Duplicate key"))
                .when(inventoryRepository).saveAll(anyList());

        // When/Then
        assertThrows(DataIntegrityViolationException.class,
                () -> inventoryService.createManyInventories(requests));

        // The repository's saveAll(...) was called once and threw
        verify(inventoryRepository, times(1)).saveAll(anyList());
    }

}
