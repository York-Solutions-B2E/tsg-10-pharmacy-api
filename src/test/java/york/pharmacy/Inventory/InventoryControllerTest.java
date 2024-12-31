package york.pharmacy.Inventory;

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
import york.pharmacy.Inventory.dto.InventoryRequest;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryService inventoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Create one Inventory -> POST /api/inventory")
    void testCreateOne() throws Exception {
        InventoryRequest request = InventoryRequest.builder()
                .medicineId(1L)
                .stockQuantity(100)
                .build();

        Inventory savedEntity = Inventory.builder()
                .id(1L)
                .medicineId(1L)
                .stockQuantity(100)
                .build();

        BDDMockito.given(inventoryService.createInventory(ArgumentMatchers.any(InventoryRequest.class)))
                .willReturn(savedEntity);

        mockMvc.perform(post("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.medicineId", is(1)))
                .andExpect(jsonPath("$.stockQuantity", is(100)));
    }

    @Test
    @DisplayName("Create many Inventories -> POST /api/inventory/bulk")
    void testCreateMany() throws Exception {
        InventoryRequest r1 = InventoryRequest.builder().medicineId(1L).stockQuantity(50).build();
        InventoryRequest r2 = InventoryRequest.builder().medicineId(2L).stockQuantity(75).build();

        Inventory e1 = Inventory.builder().id(1L).medicineId(1L).stockQuantity(50).build();
        Inventory e2 = Inventory.builder().id(2L).medicineId(2L).stockQuantity(75).build();

        BDDMockito.given(inventoryService.createManyInventories(ArgumentMatchers.anyList()))
                .willReturn(Arrays.asList(e1, e2));

        mockMvc.perform(post("/api/inventory/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Arrays.asList(r1, r2))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    @DisplayName("Read one Inventory -> GET /api/inventory/{id}")
    void testGetOne() throws Exception {
        Long id = 1L;
        Inventory entity = Inventory.builder()
                .id(id)
                .medicineId(1L)
                .stockQuantity(20)
                .build();

        BDDMockito.given(inventoryService.getInventoryById(id))
                .willReturn(entity);

        mockMvc.perform(get("/api/inventory/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.medicineId", is(1)))
                .andExpect(jsonPath("$.stockQuantity", is(20)));
    }

    @Test
    @DisplayName("Read many Inventories -> GET /api/inventory")
    void testGetAll() throws Exception {
        Inventory e1 = Inventory.builder().id(1L).medicineId(1L).stockQuantity(5).build();
        Inventory e2 = Inventory.builder().id(2L).medicineId(2L).stockQuantity(10).build();

        BDDMockito.given(inventoryService.getAllInventories())
                .willReturn(Arrays.asList(e1, e2));

        mockMvc.perform(get("/api/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].medicineId", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].medicineId", is(2)));
    }

    @Test
    @DisplayName("Update an existing Inventory -> PUT /api/inventory/{id}")
    void testUpdate() throws Exception {
        Long id = 1L;
        InventoryRequest request = InventoryRequest.builder()
                .medicineId(2L)
                .stockQuantity(99)
                .build();

        Inventory updatedEntity = Inventory.builder()
                .id(id)
                .medicineId(2L)
                .stockQuantity(99)
                .build();

        BDDMockito.given(inventoryService.updateInventory(
                        ArgumentMatchers.eq(id),
                        ArgumentMatchers.any(InventoryRequest.class)))
                .willReturn(updatedEntity);

        mockMvc.perform(put("/api/inventory/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.medicineId", is(2)))
                .andExpect(jsonPath("$.stockQuantity", is(99)));
    }

    @Test
    @DisplayName("Delete an existing Inventory -> DELETE /api/inventory/{id}")
    void testDelete() throws Exception {
        Long id = 1L;
        BDDMockito.willDoNothing().given(inventoryService).deleteInventory(id);

        mockMvc.perform(delete("/api/inventory/{id}", id))
                .andExpect(status().isNoContent());
    }
}