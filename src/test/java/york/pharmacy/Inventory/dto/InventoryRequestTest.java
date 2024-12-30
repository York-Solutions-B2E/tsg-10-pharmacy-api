package york.pharmacy.Inventory.dto;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class InventoryRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("Valid request should pass all validations")
    void testValidRequest() {
        InventoryRequest request = InventoryRequest.builder()
                .medicineId(1L)
                .stockQuantity(10)
                .build();

        Set<ConstraintViolation<InventoryRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Invalid request with null medicineId should fail validation")
    void testInvalidRequestNullMedicineId() {
        InventoryRequest request = InventoryRequest.builder()
                .medicineId(null)
                .stockQuantity(5)
                .build();

        Set<ConstraintViolation<InventoryRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        violations.forEach(v -> System.out.println(v.getPropertyPath() + " " + v.getMessage()));
    }

    @Test
    @DisplayName("Invalid request with negative stockQuantity should fail validation")
    void testInvalidRequestNegativeStockQuantity() {
        InventoryRequest request = InventoryRequest.builder()
                .medicineId(1L)
                .stockQuantity(-1)
                .build();

        Set<ConstraintViolation<InventoryRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }
}