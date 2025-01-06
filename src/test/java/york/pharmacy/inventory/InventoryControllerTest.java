package york.pharmacy.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import york.pharmacy.inventory.dto.InventoryRequest;
import york.pharmacy.inventory.dto.InventoryResponse;
import york.pharmacy.medicines.Medicine;

import java.time.Instant;
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
    private Medicine medicine;
    private int testStockQuantity;
    private InventoryRequest testRequest;
    private InventoryResponse testResponse;

    // Additional test data for bulk operations
    private InventoryRequest testRequest2;
    private InventoryResponse testResponse2;

    @BeforeEach
    void setUp() {
        testId = 1L;
        testMedicineId = 1L;
        medicine = new Medicine(1L, "Jelly Beans", "J-01", Instant.now(), Instant.now());
        testStockQuantity = 100;

        testRequest = InventoryRequest.builder()
                .medicineId(testMedicineId)
                .stockQuantity(testStockQuantity)
                .sufficientStock(false)  // Add this
                .build();

        testResponse = InventoryResponse.builder()
                .id(testId)
                .medicine(medicine)
                .stockQuantity(testStockQuantity)
                .sufficientStock(false)  // Add this
                .build();

        testRequest2 = InventoryRequest.builder()
                .medicineId(2L)
                .stockQuantity(75)
                .sufficientStock(false)  // Add this
                .build();

        Medicine medicine2 = new Medicine(2L, "Fruit Rollup", "F-01", Instant.now(), Instant.now());

        testResponse2 = InventoryResponse.builder()
                .id(2L)
                .medicine(medicine2)
                .stockQuantity(75)
                .sufficientStock(false)  // Add this
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
                .sufficientStock(false)
                .build();

        mockMvc.perform(post("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())  // This will print the full request/response details
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.medicineId", is(1)))
                .andExpect(jsonPath("$.stockQuantity", is(100)));
    }

    @Test
    @Disabled
    @DisplayName("Create many Inventories -> POST /api/inventory/bulk")
    void testCreateMany() throws Exception {
        BDDMockito.given(inventoryService.createManyInventories(ArgumentMatchers.anyList()))
                .willReturn(Arrays.asList(testResponse, testResponse2));

        // Add sufficientStock to both requests
        InventoryRequest request1 = InventoryRequest.builder()
                .medicineId(testMedicineId)
                .stockQuantity(testStockQuantity)
                .sufficientStock(false)
                .build();

        InventoryRequest request2 = InventoryRequest.builder()
                .medicineId(2L)
                .stockQuantity(75)
                .sufficientStock(false)
                .build();

        mockMvc.perform(post("/api/inventory/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Arrays.asList(request1, request2))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
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
                .andExpect(jsonPath("$.medicineId", is(1)))
                .andExpect(jsonPath("$.stockQuantity", is(100)));
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
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].medicineId", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].medicineId", is(2)));
    }

    @Test
    @Disabled
    @DisplayName("Update an existing Inventory -> PUT /api/inventory/{id}")
    void testUpdate() throws Exception {
//
//        // "setUp()" function, in beforeEach, sets original values
//
//        // This represents what we expect the service to return after the update
//        // Note that medicineId should stay the same as the original value (1L) since it's read-only
//        InventoryResponse updatedResponse = InventoryResponse.builder()
//                .id(testId)             // ID stays the same from setUp()
//                .medicineId(testMedicineId)  // Should remain 1L from setUp() since medicineId is read-only
//                .stockQuantity(99)      // Changed from 100, as compared to setUp()
//                .sufficientStock(true)  // Adding sufficientStock field
//                .build();
//
//        // This sets up our mock service layer - when the MOCK service layer's "updateInventory" method
//        // is called with (testId, any InventoryRequest), and when we tell it
//        // to return our updatedResponse object, then it should receive those two things
//        // and return updatedResponse; we're just testing the round-trip from the controller to
//        // the service and back HAPPENS AS DESIRED, not really that
//        // any of the specific updates are done or returned correctly
//        BDDMockito.given(inventoryService.updateInventory(
//                        ArgumentMatchers.eq(testId),
//                        ArgumentMatchers.any(InventoryRequest.class)))
//                .willReturn(updatedResponse);
//
//        // This represents the HTTP request body that a client would send to our API
//        // Even though the client attempts to change medicineId to 2L, it should be ignored
//        InventoryRequest updateRequest = InventoryRequest.builder()
//                .medicineId(2L)         // This attempt to change medicineId should be ignored
//                .stockQuantity(99)      // Changed from 100
//                .sufficientStock(true)  // Adding sufficientStock field
//                .build();
//
//        // This simulates the HTTP request and verifies the response;
//        // Tests that the controller returns the correct HTTP status,
//        // and that the response body matches what the service returned
//        // "mockMvc" is what tells this code to send the "updateRequest" to
//        // a mock of our "InventoryController.java"; and "@WebMvcTest", as well as
//        // "autowired private MockMvc mockMvc;" are what tell the code to set up and
//        // use a fake controller
//        mockMvc.perform(put("/api/inventory/{id}", testId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(1)))          // ID unchanged
//                .andExpect(jsonPath("$.medicineId", is(1)))  // Should remain 1 (original value) since medicineId is read-only
//                .andExpect(jsonPath("$.stockQuantity", is(99))) // Changed from 100
//                .andExpect(jsonPath("$.sufficientStock", is(true))); // Added assertion for sufficientStock
    }

    @Test
    @Disabled
    @DisplayName("Adjust stock quantity -> PUT /api/inventory/{id}/adjust-stock/{pillAdjustment}")
    void testAdjustStockQuantity() throws Exception {
//        // Set up expected response after adjustment
//        InventoryResponse adjustedResponse = InventoryResponse.builder()
//                .id(testId)
//                .medicineId(testMedicineId)
//                .stockQuantity(testStockQuantity + 50)  // Increased by 50
//                .build();
//
//        // Mock the service method
//        BDDMockito.given(inventoryService.adjustStockQuantity(
//                        ArgumentMatchers.eq(testId),
//                        ArgumentMatchers.eq(50)))
//                .willReturn(adjustedResponse);
//
//        // Perform the request and verify
//        mockMvc.perform(put("/api/inventory/{id}/adjust-stock/{pillAdjustment}", testId, 50))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(1)))
//                .andExpect(jsonPath("$.medicineId", is(1)))
//                .andExpect(jsonPath("$.stockQuantity", is(150)));  // Original 100 + 50
    }

    @Test
    @DisplayName("Delete an existing Inventory -> DELETE /api/inventory/{id}")
    void testDelete() throws Exception {
        BDDMockito.willDoNothing().given(inventoryService).deleteInventory(testId);

        mockMvc.perform(delete("/api/inventory/{id}", testId))
                .andExpect(status().isNoContent());
    }
}