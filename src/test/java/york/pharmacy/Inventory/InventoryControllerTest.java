package york.pharmacy.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import york.pharmacy.inventory.dto.InventoryRequest;
import york.pharmacy.inventory.dto.InventoryResponse;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// This annotation tells the code to test the Controller
@WebMvcTest(InventoryController.class)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Here we set up a fake service for the controller to use
    @MockBean
    private InventoryService inventoryService;

    @Autowired
    private ObjectMapper objectMapper;

    // Common test data
    private Long testId;
    private Long testMedicineId;
    private int testStockQuantity;
    private InventoryRequest testRequest;
    private InventoryResponse testResponse;

    // Additional test data for bulk operations
    private InventoryRequest testRequest2;
    private InventoryResponse testResponse2;

    @BeforeEach
    void setUp() {
        // Initialize basic test data
        testId = 1L;
        testMedicineId = 1L;
        testStockQuantity = 100;

        testRequest = InventoryRequest.builder()
                .medicineId(testMedicineId)
                .stockQuantity(testStockQuantity)
                .build();

        testResponse = InventoryResponse.builder()
                .id(testId)
                .medicineId(testMedicineId)
                .stockQuantity(testStockQuantity)
                .build();

        // Initialize secondary test data for bulk operations
        testRequest2 = InventoryRequest.builder()
                .medicineId(2L)
                .stockQuantity(75)
                .build();

        testResponse2 = InventoryResponse.builder()
                .id(2L)
                .medicineId(2L)
                .stockQuantity(75)
                .build();
    }

    @Test
    @DisplayName("Create one Inventory -> POST /api/inventory")
    void testCreateOne() throws Exception {
        BDDMockito.given(inventoryService.createInventory(ArgumentMatchers.any(InventoryRequest.class)))
                .willReturn(testResponse);

        mockMvc.perform(post("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.medicineId", is(1)))
                .andExpect(jsonPath("$.stockQuantity", is(100)));
    }

    @Test
    @DisplayName("Create many Inventories -> POST /api/inventory/bulk")
    void testCreateMany() throws Exception {
        BDDMockito.given(inventoryService.createManyInventories(ArgumentMatchers.anyList()))
                .willReturn(Arrays.asList(testResponse, testResponse2));

        mockMvc.perform(post("/api/inventory/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Arrays.asList(testRequest, testRequest2))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    @DisplayName("Read one Inventory -> GET /api/inventory/{id}")
    void testGetOne() throws Exception {
        BDDMockito.given(inventoryService.getInventoryById(testId))
                .willReturn(testResponse);

        mockMvc.perform(get("/api/inventory/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.medicineId", is(1)))
                .andExpect(jsonPath("$.stockQuantity", is(100)));
    }

    @Test
    @DisplayName("Read many Inventories -> GET /api/inventory")
    void testGetAll() throws Exception {
        BDDMockito.given(inventoryService.getAllInventories())
                .willReturn(Arrays.asList(testResponse, testResponse2));

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

        // "setUp()" function, in beforeEach, sets original values

        // This represents what we expect the service to return after the update
        InventoryResponse updatedResponse = InventoryResponse.builder()
                .id(testId)             // ID stays the same from setUp()
                .medicineId(2L)         // Changed from 1L, as compared to setUp()
                .stockQuantity(99)      // Changed from 100, as compared to setUp()
                .build();

        // This sets up our mock service layer - when the MOCK service layer's "updateInventory" method
        // is called with (testId, any InventoryRequest), and when we tell it
        // to return our updatedResponse object, then it should receive those two things
        // and return updatedResponse; we're just testing the round-trip from the controller to
        // the service and back HAPPENS AS DESIRED, not really that
        // any of the specific updates are done or returned correctly
        BDDMockito.given(inventoryService.updateInventory(
                        ArgumentMatchers.eq(testId),
                        ArgumentMatchers.any(InventoryRequest.class)))
                .willReturn(updatedResponse);

        // This represents the HTTP request body that a client would send to our API
        InventoryRequest updateRequest = InventoryRequest.builder()
                .medicineId(2L)         // Changed from 1L
                .stockQuantity(99)      // Changed from 100
                .build();

        // This simulates the HTTP request and verifies the response;
        // Tests that the controller returns the correct HTTP status,
        // and that the response body matches what the service returned
        // "mockMvc" is what tells this code to send the "updateRequest" to
        // a mock of our "InventoryController.java"; and "@WebMvcTest", as well as
        // "autowired private MockMvc mockMvc;" are what tell the code to set up and
        // use a fake controller
        mockMvc.perform(put("/api/inventory/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))          // ID unchanged
                .andExpect(jsonPath("$.medicineId", is(2)))  // Changed from 1
                .andExpect(jsonPath("$.stockQuantity", is(99))); // Changed from 100
    }

    @Test
    @DisplayName("Delete an existing Inventory -> DELETE /api/inventory/{id}")
    void testDelete() throws Exception {
        BDDMockito.willDoNothing().given(inventoryService).deleteInventory(testId);

        mockMvc.perform(delete("/api/inventory/{id}", testId))
                .andExpect(status().isNoContent());
    }
}