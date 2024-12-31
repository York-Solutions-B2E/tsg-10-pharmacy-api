// InventoryServiceTest.java
package york.pharmacy.Inventory;

import york.pharmacy.Inventory.dto.InventoryRequest;
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

    @Test
    @DisplayName("Should create a new Inventory successfully")
    void testCreateInventory() {
        // Given
        Long medicineId = 1L;
        int stockQuantity = 10;

        Inventory mockEntity = Inventory.builder()
                .id(1L)
                .medicineId(medicineId)
                .stockQuantity(stockQuantity)
                .build();

        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(mockEntity);

        InventoryRequest request = InventoryRequest.builder()
                .medicineId(medicineId)
                .stockQuantity(stockQuantity)
                .build();

        // When
        Inventory result = inventoryService.createInventory(request);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(medicineId, result.getMedicineId());
        assertEquals(stockQuantity, result.getStockQuantity());
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    @DisplayName("Should get an existing Inventory by ID")
    void testGetInventoryById() {
        // Given
        Long id = 1L;
        Inventory mockEntity = Inventory.builder()
                .id(id)
                .medicineId(1L)
                .stockQuantity(5)
                .build();

        when(inventoryRepository.findById(id)).thenReturn(Optional.of(mockEntity));

        // When
        Inventory result = inventoryService.getInventoryById(id);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getMedicineId());
        verify(inventoryRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should retrieve all Inventory entries")
    void testGetAllInventories() {
        // Given
        Inventory i1 = Inventory.builder().id(1L).medicineId(1L).stockQuantity(10).build();
        Inventory i2 = Inventory.builder().id(2L).medicineId(2L).stockQuantity(20).build();

        when(inventoryRepository.findAll()).thenReturn(Arrays.asList(i1, i2));

        // When
        var results = inventoryService.getAllInventories();

        // Then
        assertNotNull(results);
        assertEquals(2, results.size());
        verify(inventoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should update an existing Inventory successfully")
    void testUpdateInventory() {
        // Given
        Long existingId = 1L;
        Inventory existingEntity = Inventory.builder()
                .id(existingId)
                .medicineId(1L)
                .stockQuantity(5)
                .build();

        when(inventoryRepository.findById(existingId))
                .thenReturn(Optional.of(existingEntity));
        when(inventoryRepository.save(existingEntity))
                .thenReturn(existingEntity);

        InventoryRequest updateRequest = InventoryRequest.builder()
                .medicineId(2L)
                .stockQuantity(10)
                .build();

        // When
        Inventory result = inventoryService.updateInventory(existingId, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(2L, result.getMedicineId());
        assertEquals(10, result.getStockQuantity());
        verify(inventoryRepository).findById(existingId);
        verify(inventoryRepository).save(existingEntity);
    }

    @Test
    @DisplayName("Should delete an existing Inventory by ID")
    void testDeleteInventory() {
        // Given
        when(inventoryRepository.existsById(1L)).thenReturn(true);

        // When
        inventoryService.deleteInventory(1L);

        // Then
        verify(inventoryRepository).existsById(1L);
        verify(inventoryRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting a non-existing Inventory")
    void testDeleteInventoryNotFound() {
        // Given
        when(inventoryRepository.existsById(999L)).thenReturn(false);

        // When/Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> inventoryService.deleteInventory(999L));

        assertEquals("Inventory not found with id: 999", ex.getMessage());
        verify(inventoryRepository).existsById(999L);
        verify(inventoryRepository, never()).deleteById(any());
    }
}