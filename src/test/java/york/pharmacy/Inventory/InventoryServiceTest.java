package york.pharmacy.inventory;

import york.pharmacy.inventory.dto.InventoryRequest;
import york.pharmacy.inventory.dto.InventoryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @InjectMocks
    private InventoryService inventoryService;

    // Common test data
    private Long testId;
    private Long testMedicineId;
    private int testStockQuantity;
    private Inventory testInventory;
    private InventoryRequest testRequest;
    private InventoryResponse expectedResponse;

    @BeforeEach
    void setUp() {
        // Initialize test data
        testId = 1L;
        testMedicineId = 1L;
        testStockQuantity = 10;

        testInventory = Inventory.builder()
                .id(testId)
                .medicineId(testMedicineId)
                .stockQuantity(testStockQuantity)
                .build();

        testRequest = InventoryRequest.builder()
                .medicineId(testMedicineId)
                .stockQuantity(testStockQuantity)
                .build();

        expectedResponse = InventoryResponse.builder()
                .id(testId)
                .medicineId(testMedicineId)
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
        assertEquals(testMedicineId, result.getMedicineId());
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
                .medicineId(2L)
                .stockQuantity(20)
                .build();
        List<Inventory> savedEntities = Arrays.asList(testInventory, secondInventory);

        when(inventoryRepository.saveAll(any())).thenReturn(savedEntities);

        // When
        List<InventoryResponse> results = inventoryService.createManyInventories(requests);

        // Then
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(testMedicineId, results.get(0).getMedicineId());
        assertEquals(2L, results.get(1).getMedicineId());
        verify(inventoryRepository, times(1)).saveAll(any());
    }

    @Test
    @DisplayName("Should get an existing Inventory by ID")
    void testGetInventoryById() {
        // Given
        when(inventoryRepository.findById(testId))
                .thenReturn(Optional.of(testInventory));

        // When
        InventoryResponse result = inventoryService.getInventoryById(testId);

        // Then
        assertNotNull(result);
        assertEquals(testMedicineId, result.getMedicineId());
        verify(inventoryRepository, times(1)).findById(testId);
    }

    @Test
    @DisplayName("Should retrieve all Inventory entries")
    void testGetAllInventories() {
        // Given
        Inventory secondInventory = Inventory.builder()
                .id(2L)
                .medicineId(2L)
                .stockQuantity(20)
                .build();

        when(inventoryRepository.findAll())
                .thenReturn(Arrays.asList(testInventory, secondInventory));

        // When
        var results = inventoryService.getAllInventories();

        // Then
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(testMedicineId, results.get(0).getMedicineId());
        assertEquals(2L, results.get(1).getMedicineId());
        verify(inventoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should update an existing Inventory successfully while preserving medicineId")
    void testUpdateInventory() {
        // Given
        InventoryRequest updateRequest = InventoryRequest.builder()
                .medicineId(2L)  // This should be ignored in update
                .stockQuantity(20)
                .sufficientStock(true)
                .build();

        Inventory updatedInventory = Inventory.builder()
                .id(testId)
                .medicineId(testMedicineId)  // Should keep original medicineId
                .stockQuantity(20)
                .sufficientStock(true)
                .build();

        when(inventoryRepository.findById(testId))
                .thenReturn(Optional.of(testInventory));
        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(updatedInventory);

        // When
        InventoryResponse result = inventoryService.updateInventory(testId, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(testMedicineId, result.getMedicineId());  // Should keep original medicineId
        assertEquals(20, result.getStockQuantity());
        assertTrue(result.getSufficientStock());
        verify(inventoryRepository).findById(testId);
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    @DisplayName("Should ignore medicineId in update request and preserve original value")
    void testUpdateInventoryPreservesMedicineId() {
        // Given
        Long originalMedicineId = testMedicineId;  // 1L from setUp()
        Long attemptedNewMedicineId = 999L;        // Attempting to change to this

        InventoryRequest updateRequest = InventoryRequest.builder()
                .medicineId(attemptedNewMedicineId) // Attempting to update medicineId
                .stockQuantity(50)                  // Only this should change
                .sufficientStock(true)              // And this
                .build();

        // The inventory that should be saved - note medicineId stays the same
        Inventory expectedSavedInventory = Inventory.builder()
                .id(testId)
                .medicineId(originalMedicineId)     // Should keep original medicineId
                .stockQuantity(50)                  // These other fields
                .sufficientStock(true)              // should update
                .build();

        when(inventoryRepository.findById(testId))
                .thenReturn(Optional.of(testInventory));
        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(expectedSavedInventory);

        // When
        InventoryResponse result = inventoryService.updateInventory(testId, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(originalMedicineId, result.getMedicineId(),
                "MedicineId should remain unchanged despite update attempt");
        assertEquals(50, result.getStockQuantity(),
                "Stock quantity should be updated");
        assertTrue(result.getSufficientStock(),
                "Sufficient stock should be updated");

        verify(inventoryRepository).findById(testId);
        verify(inventoryRepository).save(argThat(savedInventory ->
                savedInventory.getMedicineId().equals(originalMedicineId) &&
                        savedInventory.getStockQuantity() == 50 &&
                        savedInventory.getSufficientStock()
        ));
    }

    @Test
    @DisplayName("Should delete an existing Inventory by ID")
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
    @DisplayName("Should update sufficient stock status correctly")
    void testUpdateSufficientStock() {
        // Given
        HashMap<Long, Integer> medicineCount = new HashMap<>();
        medicineCount.put(testMedicineId, 5);

        Inventory inventoryWithSufficientStock = Inventory.builder()
                .id(testId)
                .medicineId(testMedicineId)
                .stockQuantity(testStockQuantity)
                .sufficientStock(true)
                .build();

        when(inventoryRepository.findByMedicineId(testMedicineId))
                .thenReturn(Optional.of(testInventory));
        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(inventoryWithSufficientStock);

        // When
        InventoryResponse result = inventoryService.updateSufficientStock(medicineCount);

        // Then
        assertNotNull(result);
        assertTrue(result.getSufficientStock());
        verify(inventoryRepository).findByMedicineId(testMedicineId);
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    @DisplayName("Should adjust stock quantity successfully")
    void testAdjustStockQuantity() {
        // Given
        int adjustment = 5;
        Inventory adjustedInventory = Inventory.builder()
                .id(testId)
                .medicineId(testMedicineId)
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
}