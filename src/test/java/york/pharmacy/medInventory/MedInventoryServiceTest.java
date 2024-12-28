package york.pharmacy.medInventory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedInventoryServiceTest {

    @Mock
    private MedInventoryRepository medInventoryRepository;

    @InjectMocks
    private MedInventoryService medInventoryService;

    @Test
    @DisplayName("Should create a new MedInventory successfully")
    void testCreateMedInventoryEntity() {
        // GIVEN
        String medName = "TestMed";
        int stockCount = 10;
        LocalDate deliveryDate = LocalDate.now().plusDays(2);

        // We want the repository.save(...) to return an entity with ID = 1
        MedInventoryEntity mockEntity = MedInventoryEntity.builder()
                .id(1L)
                .medName(medName)
                .stockCount(stockCount)
                .deliveryDate(deliveryDate)
                .build();

        when(medInventoryRepository.save(any(MedInventoryEntity.class)))
                .thenReturn(mockEntity);

        // Add debugging
        try {
            MedInventoryEntity result = medInventoryService.createMedInventoryEntity(medName, stockCount, deliveryDate);
            System.out.println("Result: " + result);  // See if we get here
            assertNotNull(result, "Returned entity should not be null");
            assertEquals(1L, result.getId(), "Expected ID = 1");
            assertEquals("TestMed", result.getMedName());
            assertEquals(10, result.getStockCount());
        } catch (Exception e) {
            e.printStackTrace();  // This will show if there's an exception
            throw e;
        }

        // Verify repository call
        verify(medInventoryRepository, times(1)).save(any(MedInventoryEntity.class));
    }

    @Test
    @DisplayName("Should get an existing MedInventory by ID")
    void testGetMedInventoryEntityById() {
        // GIVEN
        Long id = 1L;
        MedInventoryEntity mockEntity = MedInventoryEntity.builder()
                .id(id)
                .medName("SampleMed")
                .stockCount(5)
                .build();

        when(medInventoryRepository.findById(id)).thenReturn(Optional.of(mockEntity));

        // WHEN
        MedInventoryEntity result = medInventoryService.getMedInventoryEntityById(id);

        // THEN
        assertNotNull(result);
        assertEquals("SampleMed", result.getMedName());
        verify(medInventoryRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should throw exception if MedInventory by ID not found")
    void testGetMedInventoryEntityByIdNotFound() {
        // GIVEN
        Long missingId = 999L;
        when(medInventoryRepository.findById(missingId)).thenReturn(Optional.empty());

        // WHEN / THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> medInventoryService.getMedInventoryEntityById(missingId));

        assertEquals("No MedInventory found with id: 999", ex.getMessage());
        verify(medInventoryRepository, times(1)).findById(missingId);
    }

    @Test
    @DisplayName("Should retrieve all MedInventory entries")
    void testGetAllMedInventoryEntities() {
        // GIVEN
        MedInventoryEntity m1 = MedInventoryEntity.builder().id(1L).medName("Med1").stockCount(10).build();
        MedInventoryEntity m2 = MedInventoryEntity.builder().id(2L).medName("Med2").stockCount(20).build();

        when(medInventoryRepository.findAll()).thenReturn(Arrays.asList(m1, m2));

        // WHEN
        var results = medInventoryService.getAllMedInventoryEntities();

        // THEN
        assertNotNull(results);
        assertEquals(2, results.size());
        verify(medInventoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list if no MedInventory entries found")
    void testGetAllMedInventoryEntitiesEmpty() {
        // GIVEN
        when(medInventoryRepository.findAll()).thenReturn(Collections.emptyList());

        // WHEN
        var results = medInventoryService.getAllMedInventoryEntities();

        // THEN
        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(medInventoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should update an existing MedInventory successfully")
    void testUpdateMedInventoryEntity() {
        // GIVEN
        Long existingId = 1L;
        MedInventoryEntity existingEntity = MedInventoryEntity.builder()
                .id(existingId)
                .medName("OldName")
                .stockCount(5)
                .build();

        // The repository should return the existing entity
        when(medInventoryRepository.findById(existingId))
                .thenReturn(Optional.of(existingEntity));

        // After saving, we assume it returns the same entity (with updated fields).
        when(medInventoryRepository.save(existingEntity))
                .thenReturn(existingEntity);

        // WHEN
        MedInventoryEntity result = medInventoryService.updateMedInventoryEntity(
                existingId, "NewName", 20, LocalDate.now().plusDays(5));

        // THEN
        assertNotNull(result);
        assertEquals("NewName", result.getMedName());
        assertEquals(20, result.getStockCount());
        verify(medInventoryRepository, times(1)).findById(existingId);
        verify(medInventoryRepository, times(1)).save(existingEntity);
    }

    @Test
    @DisplayName("Should throw exception when updating a non-existing MedInventory")
    void testUpdateMedInventoryEntityNotFound() {
        // GIVEN
        Long nonExistingId = 999L;
        when(medInventoryRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // WHEN / THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> medInventoryService.updateMedInventoryEntity(nonExistingId, "AnyName", 10, null));

        assertEquals("No MedInventory found with id: 999", ex.getMessage());
        verify(medInventoryRepository, times(1)).findById(nonExistingId);
        // We do NOT call save(...) in this scenario
        verify(medInventoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete an existing MedInventory by ID")
    void testDeleteMedInventoryEntity() {
        // GIVEN
        when(medInventoryRepository.existsById(1L)).thenReturn(true);

        // WHEN
        medInventoryService.deleteMedInventoryEntity(1L);

        // THEN
        verify(medInventoryRepository, times(1)).existsById(1L);
        verify(medInventoryRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting a non-existing MedInventory")
    void testDeleteMedInventoryEntityNotFound() {
        // GIVEN
        when(medInventoryRepository.existsById(999L)).thenReturn(false);

        // WHEN / THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> medInventoryService.deleteMedInventoryEntity(999L));

        assertEquals("No MedInventory found with id: 999", ex.getMessage());
        verify(medInventoryRepository, times(1)).existsById(999L);
        verify(medInventoryRepository, never()).deleteById(999L);
    }
}
