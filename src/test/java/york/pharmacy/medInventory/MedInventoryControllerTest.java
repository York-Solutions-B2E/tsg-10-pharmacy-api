package york.pharmacy.medInventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import york.pharmacy.medInventory.MedInventoryService;
import york.pharmacy.medInventory.MedInventoryEntity;
import york.pharmacy.medInventory.dto.MedInventoryRequest;
import york.pharmacy.medInventory.dto.MedInventoryResponse;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests the MedInventoryController endpoints (CCRRUD):
 *  Create One, Create Many, Read One, Read Many, Update, Delete.
 */
@WebMvcTest(MedInventoryController.class)
class MedInventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedInventoryService medInventoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Create one MedInventoryEntity -> POST /api/med-inventory")
    void testCreateOne() throws Exception {
        // Given a valid request
        MedInventoryRequest request = MedInventoryRequest.builder()
                .medName("Aspirin")
                .stockCount(100)
                .deliveryDate(LocalDate.of(2025, 1, 10))
                .build();

        // The service will return an entity with ID = 1
        MedInventoryEntity savedEntity = MedInventoryEntity.builder()
                .id(1L)
                .medName("Aspirin")
                .stockCount(100)
                .deliveryDate(LocalDate.of(2025, 1, 10))
                .build();

        BDDMockito.given(medInventoryService.createMedInventoryEntity(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.any(LocalDate.class))
        ).willReturn(savedEntity);

        // When we call POST /api/med-inventory
        mockMvc.perform(post("/api/med-inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                // Then we expect HTTP 201 and a response body with ID, medName, etc.
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.medName", is("Aspirin")))
                .andExpect(jsonPath("$.stockCount", is(100)));
    }

    @Test
    @DisplayName("Create many MedInventoryEntities -> POST /api/med-inventory/bulk")
    void testCreateMany() throws Exception {
        // Given a list of requests
        MedInventoryRequest r1 = MedInventoryRequest.builder().medName("MedA").stockCount(50).build();
        MedInventoryRequest r2 = MedInventoryRequest.builder().medName("MedB").stockCount(75).build();
        List<MedInventoryRequest> requestList = Arrays.asList(r1, r2);

        // The service will return corresponding entities with IDs
        MedInventoryEntity e1 = MedInventoryEntity.builder().id(1L).medName("MedA").stockCount(50).build();
        MedInventoryEntity e2 = MedInventoryEntity.builder().id(2L).medName("MedB").stockCount(75).build();
        List<MedInventoryEntity> savedEntities = Arrays.asList(e1, e2);

        BDDMockito.given(medInventoryService.createManyMedInventories(ArgumentMatchers.anyList()))
                .willReturn(savedEntities);

        // When we call POST /api/med-inventory/bulk
        mockMvc.perform(post("/api/med-inventory/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestList))
                )
                // Then we expect HTTP 201 and a list of JSON objects
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    @DisplayName("Read one MedInventoryEntity -> GET /api/med-inventory/{id}")
    void testGetOne() throws Exception {
        // Given
        Long id = 1L;
        MedInventoryEntity entity = MedInventoryEntity.builder()
                .id(id)
                .medName("Ibuprofen")
                .stockCount(20)
                .build();

        BDDMockito.given(medInventoryService.getMedInventoryEntityById(id))
                .willReturn(entity);

        // When we call GET /api/med-inventory/1
        mockMvc.perform(get("/api/med-inventory/{id}", id))
                // Then we expect 200 and the JSON body
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.medName", is("Ibuprofen")))
                .andExpect(jsonPath("$.stockCount", is(20)));
    }

    @Test
    @DisplayName("Read many MedInventoryEntities -> GET /api/med-inventory")
    void testGetAll() throws Exception {
        // Given
        MedInventoryEntity e1 = MedInventoryEntity.builder().id(1L).medName("Item1").stockCount(5).build();
        MedInventoryEntity e2 = MedInventoryEntity.builder().id(2L).medName("Item2").stockCount(10).build();

        BDDMockito.given(medInventoryService.getAllMedInventoryEntities())
                .willReturn(Arrays.asList(e1, e2));

        // When we call GET /api/med-inventory
        mockMvc.perform(get("/api/med-inventory"))
                // Then we expect 200 and a list of JSON
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].medName", is("Item1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].medName", is("Item2")));
    }

    @Test
    @DisplayName("Update an existing MedInventoryEntity -> PUT /api/med-inventory/{id}")
    void testUpdate() throws Exception {
        // Given
        Long id = 1L;
        MedInventoryRequest request = MedInventoryRequest.builder()
                .medName("UpdatedName")
                .stockCount(99)
                .deliveryDate(LocalDate.of(2026, 1, 1))
                .build();

        MedInventoryEntity updatedEntity = MedInventoryEntity.builder()
                .id(id)
                .medName("UpdatedName")
                .stockCount(99)
                .deliveryDate(LocalDate.of(2026, 1, 1))
                .build();

        BDDMockito.given(medInventoryService.updateMedInventoryEntity(
                ArgumentMatchers.eq(id),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.any(LocalDate.class))
        ).willReturn(updatedEntity);

        // When we call PUT /api/med-inventory/1
        mockMvc.perform(put("/api/med-inventory/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.medName", is("UpdatedName")))
                .andExpect(jsonPath("$.stockCount", is(99)));
    }

    @Test
    @DisplayName("Delete an existing MedInventoryEntity -> DELETE /api/med-inventory/{id}")
    void testDelete() throws Exception {
        // Given
        Long id = 1L;
        BDDMockito.willDoNothing().given(medInventoryService).deleteMedInventoryEntity(id);

        // When
        mockMvc.perform(delete("/api/med-inventory/{id}", id))
                // Then
                .andExpect(status().isNoContent());
    }
}
