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
    @DisplayName("Should update an existing Inventory successfully")
    void testUpdateInventory() {
        // Given
        InventoryRequest updateRequest = InventoryRequest.builder()
                .medicineId(2L)
                .stockQuantity(20)
                .build();

        Inventory updatedInventory = Inventory.builder()
                .id(testId)
                .medicineId(2L)
                .stockQuantity(20)
                .build();

        when(inventoryRepository.findById(testId))
                .thenReturn(Optional.of(testInventory));
        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(updatedInventory);

        // When
        InventoryResponse result = inventoryService.updateInventory(testId, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(2L, result.getMedicineId());
        assertEquals(20, result.getStockQuantity());
        verify(inventoryRepository).findById(testId);
        verify(inventoryRepository).save(any(Inventory.class));
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
}