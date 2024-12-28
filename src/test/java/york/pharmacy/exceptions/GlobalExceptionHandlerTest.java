package york.pharmacy.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    /** Test: handleValidationException (MethodArgumentNotValidException) */
    @Test
    void testHandleValidationException() {
        // Arrange
        FieldError fieldError = new FieldError("taskRequest", "title", "must not be null");
        BindException bindException = mock(BindException.class);
        when(bindException.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindException);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleValidationException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation Failed", ((java.util.Map<?, ?>) response.getBody()).get("message"));
        assertEquals("must not be null", ((java.util.Map<?, ?>) ((java.util.Map<?, ?>) response.getBody()).get("details")).get("title"));
    }

    /** Test: handleIllegalArgumentException */
    @Test
    void testHandleIllegalArgumentException() {
        // Arrange
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleIllegalArgument(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid argument", ((java.util.Map<?, ?>) response.getBody()).get("message"));
    }

    /** Test: handleResourceNotFound */
    @Test
    void testHandleResourceNotFound() {
        // Arrange
        ResourceNotFoundException ex = new ResourceNotFoundException("Task not found");

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleResourceNotFound(ex);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Task not found", ((java.util.Map<?, ?>) response.getBody()).get("message"));
    }

    /** Test: handleRuntimeException */
    @Test
    void testHandleRuntimeException() {
        // Arrange
        RuntimeException ex = new RuntimeException("Something went wrong");

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleRuntimeException(ex);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal Server Error", ((java.util.Map<?, ?>) response.getBody()).get("message"));
        assertEquals("Something went wrong", ((java.util.Map<?, ?>) response.getBody()).get("details"));
    }

    /** Test: handleGlobalException */
    @Test
    void testHandleGlobalException() {
        // Arrange
        Exception ex = new Exception("Unexpected error occurred");

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleGlobalException(ex);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred", ((java.util.Map<?, ?>) response.getBody()).get("message"));
        assertEquals("Unexpected error occurred", ((java.util.Map<?, ?>) response.getBody()).get("details"));
    }
}

