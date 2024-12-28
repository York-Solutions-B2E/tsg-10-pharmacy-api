package york.pharmacy.medInventory.dto;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MedInventoryRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        // Build a Validator for testing Bean Validation constraints
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("Valid request should pass all validations")
    void testValidRequest() {
        MedInventoryRequest request = MedInventoryRequest.builder()
                .medName("Aspirin")
                .stockCount(10)
                .deliveryDate(LocalDate.of(2024, 12, 31))
                .build();

        Set<ConstraintViolation<MedInventoryRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "No violations expected for a valid request");
    }

    @Test
    @DisplayName("Invalid request with null medName should fail validation")
    void testInvalidRequestNullMedName() {
        MedInventoryRequest request = MedInventoryRequest.builder()
                .medName(null)  // violates @NotNull
                .stockCount(5)
                .deliveryDate(null)  // optional, so no violation here
                .build();

        Set<ConstraintViolation<MedInventoryRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Should have violations due to null medName");

        // Example: print out the violation messages
        violations.forEach(v -> System.out.println(v.getPropertyPath() + " " + v.getMessage()));
    }

    @Test
    @DisplayName("Invalid request with negative stockCount should fail validation")
    void testInvalidRequestNegativeStockCount() {
        MedInventoryRequest request = MedInventoryRequest.builder()
                .medName("Test Med")
                .stockCount(-1)  // violates @Min(0)
                .build();

        Set<ConstraintViolation<MedInventoryRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Should have violations due to negative stockCount");
    }
}
