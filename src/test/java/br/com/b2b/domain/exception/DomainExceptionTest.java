package br.com.b2b.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomainExceptionTest {

    @Test
    @DisplayName("Should create exception with only a message")
    void shouldCreateExceptionWithMessage() {
        // Arrange
        String expectedMessage = "This is a domain error message.";

        // Act
        DomainException exception = new DomainException(expectedMessage);

        // Assert
        assertEquals(expectedMessage, exception.getMessage());
        assertNull(exception.getCause(), "The cause should be null when not provided.");
    }

    @Test
    @DisplayName("Should create exception with a message and a cause")
    void shouldCreateExceptionWithMessageAndCause() {
        // Arrange
        String expectedMessage = "This is a domain error with a cause.";
        Throwable expectedCause = new RuntimeException("The root cause of the error.");

        // Act
        DomainException exception = new DomainException(expectedMessage, expectedCause);

        // Assert
        assertEquals(expectedMessage, exception.getMessage());
        assertEquals(expectedCause, exception.getCause(), "The cause should be the one provided in the constructor.");
    }
}
