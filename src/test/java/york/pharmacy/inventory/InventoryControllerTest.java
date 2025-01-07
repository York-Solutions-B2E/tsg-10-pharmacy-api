package york.pharmacy.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import york.pharmacy.inventory.dto.InventoryRequest;
import york.pharmacy.inventory.dto.InventoryResponse;
import york.pharmacy.medicines.Medicine;

import java.time.Instant;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class InventoryControllerTest {

    @InjectMocks
    private InventoryController inventoryController;

    @Mock
    private InventoryService inventoryService;

    private MockMvc mockMvc;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Common test data
    private Long testId;
    private Long testMedicineId;
    private Medicine medicine;
    private int testStockQuantity;
    private InventoryRequest testRequest;
    private InventoryResponse testResponse;

    // Additional test data for bulk operations
    private InventoryRequest testRequest2;
    private InventoryResponse testResponse2;

    @BeforeEach
    void setUp() {
        // Build MockMvc using the standaloneSetup approach
        mockMvc = MockMvcBuilders.standaloneSetup(inventoryController).build();

        testId = 1L;
        testMedicineId = 1L;
        medicine = new Medicine(1L, "Jelly Beans", "J-01", Instant.now(), Instant.now());
        testStockQuantity = 100;

        testRequest = InventoryRequest.builder()
                .medicineId(testMedicineId)
                .stockQuantity(testStockQuantity)
                .build();

        testResponse = InventoryResponse.builder()
                .id(testId)
                .medicine(medicine)
                .stockQuantity(testStockQuantity)
                .sufficientStock(false)
                .build();

        testRequest2 = InventoryRequest.builder()
                .medicineId(2L)
                .stockQuantity(75)
                .build();

        Medicine medicine2 = new Medicine(2L, "Fruit Rollup", "F-01", Instant.now(), Instant.now());
        testResponse2 = InventoryResponse.builder()
                .id(2L)
                .medicine(medicine2)
                .stockQuantity(75)
                .sufficientStock(false)
                .build();
    }

    @Test
    @Disabled
    @DisplayName("Create one Inventory -> POST /api/inventory")
    void testCreateOne() throws Exception {
        BDDMockito.given(inventoryService.createInventory(ArgumentMatchers.any(InventoryRequest.class)))
                .willReturn(testResponse);

        InventoryRequest request = InventoryRequest.builder()
                .medicineId(testMedicineId)
                .stockQuantity(testStockQuantity)
                .build();

        mockMvc.perform(post("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                // Changed this to match the nested Medicine object
                .andExpect(jsonPath("$.medicine.id", is(1)))
                .andExpect(jsonPath("$.stockQuantity", is(100)))
                .andExpect(jsonPath("$.sufficientStock", is(false)));
    }

    @Test
    @Disabled
    @DisplayName("Create many Inventories -> POST /api/inventory/bulk")
    void testCreateMany() throws Exception {
        BDDMockito.given(inventoryService.createManyInventories(ArgumentMatchers.anyList()))
                .willReturn(Arrays.asList(testResponse, testResponse2));

        InventoryRequest request1 = InventoryRequest.builder()
                .medicineId(testMedicineId)
                .stockQuantity(testStockQuantity)
                .build();
        InventoryRequest request2 = InventoryRequest.builder()
                .medicineId(2L)
                .stockQuantity(75)
                .build();

        mockMvc.perform(post("/api/inventory/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Arrays.asList(request1, request2))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()", is(2)))
                // For first response
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].medicine.id", is(1)))
                .andExpect(jsonPath("$[0].stockQuantity", is(100)))
                .andExpect(jsonPath("$[0].sufficientStock", is(false)))
                // For second response
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].medicine.id", is(2)))
                .andExpect(jsonPath("$[1].stockQuantity", is(75)))
                .andExpect(jsonPath("$[1].sufficientStock", is(false)));
    }

    @Test
    @Disabled
    @DisplayName("Read one Inventory -> GET /api/inventory/{id}")
    void testGetOne() throws Exception {
        BDDMockito.given(inventoryService.getInventoryById(testId))
                .willReturn(testResponse);

        mockMvc.perform(get("/api/inventory/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.medicine.id", is(1)))
                .andExpect(jsonPath("$.stockQuantity", is(100)))
                .andExpect(jsonPath("$.sufficientStock", is(false)));
    }

    @Test
    @Disabled
    @DisplayName("Read many Inventories -> GET /api/inventory")
    void testGetAll() throws Exception {
        BDDMockito.given(inventoryService.getAllInventories())
                .willReturn(Arrays.asList(testResponse, testResponse2));

        mockMvc.perform(get("/api/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                // First entry
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].medicine.id", is(1)))
                .andExpect(jsonPath("$[0].stockQuantity", is(100)))
                .andExpect(jsonPath("$[0].sufficientStock", is(false)))
                // Second entry
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].medicine.id", is(2)))
                .andExpect(jsonPath("$[1].stockQuantity", is(75)))
                .andExpect(jsonPath("$[1].sufficientStock", is(false)));
    }

    @Test
    @Disabled
    @DisplayName("Update an existing Inventory -> PUT /api/inventory/{id}")
    void testUpdate() throws Exception {

        // "setUp()" function, in beforeEach, sets original values
        // This represents what we expect the service to return after the update
        InventoryResponse updatedResponse = InventoryResponse.builder()
                .id(testId)
                .medicine(
                        // Keep the same medicine object or at least same ID
                        new Medicine(testMedicineId, "Jelly Beans", "J-01", Instant.now(), Instant.now())
                )
                .stockQuantity(99)      // changed from 100
                .sufficientStock(true)
                .build();

        // Mock service layer
        BDDMockito.given(inventoryService.updateInventory(
                        ArgumentMatchers.eq(testId),
                        ArgumentMatchers.any(InventoryRequest.class)))
                .willReturn(updatedResponse);

        // This represents the HTTP request body that a client would send
        InventoryRequest updateRequest = InventoryRequest.builder()
                .medicineId(2L)         // attempt to change medicineId, but service ignores it
                .stockQuantity(99)
                .build();

        mockMvc.perform(put("/api/inventory/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                // Should remain the original medicine ID in the DB (or the one in updatedResponse)
                .andExpect(jsonPath("$.medicine.id", is(1)))
                .andExpect(jsonPath("$.stockQuantity", is(99)))
                .andExpect(jsonPath("$.sufficientStock", is(true)));
    }

    @Test
    @Disabled
    @DisplayName("Adjust stock quantity -> PUT /api/inventory/{id}/adjust-stock/{pillAdjustment}")
    void testAdjustStockQuantity() throws Exception {
        // Set up expected response after adjustment
        InventoryResponse adjustedResponse = InventoryResponse.builder()
                .id(testId)
                .medicine(medicine)
                .stockQuantity(testStockQuantity + 50)  // 100 + 50 = 150
                .sufficientStock(false)
                .build();

        // Mock the service method
        BDDMockito.given(inventoryService.adjustStockQuantity(testId, 50))
                .willReturn(adjustedResponse);

        // Perform the request and verify
        mockMvc.perform(put("/api/inventory/{id}/adjust-stock/{pillAdjustment}", testId, 50))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.medicine.id", is(1)))
                .andExpect(jsonPath("$.stockQuantity", is(150)))
                .andExpect(jsonPath("$.sufficientStock", is(false)));
    }

    @Test
    @DisplayName("Delete an existing Inventory -> DELETE /api/inventory/{id}")
    void testDelete() throws Exception {
        BDDMockito.willDoNothing().given(inventoryService).deleteInventory(testId);

        mockMvc.perform(delete("/api/inventory/{id}", testId))
                .andExpect(status().isNoContent());
    }
}
