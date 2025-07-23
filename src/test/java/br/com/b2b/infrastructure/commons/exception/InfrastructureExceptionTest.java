package br.com.b2b.infrastructure.commons.exception;


import br.com.b2b.infrastructure.commons.exeception.InfrastructureException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InfrastructureExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        // Arrange
        String expectedMessage = "A specific infrastructure error occurred.";

        // Act
        InfrastructureException exception = new InfrastructureException(expectedMessage);

        // Assert
        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    void shouldCreateExceptionWithMessageAndCause() {
        // Arrange
        String expectedMessage = "Infrastructure failed because of an underlying issue.";
        Throwable expectedCause = new NullPointerException("The root cause of the failure.");

        // Act
        InfrastructureException exception = new InfrastructureException(expectedMessage, expectedCause);

        // Assert
        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
        assertThat(exception.getCause()).isNotNull();
        assertThat(exception.getCause()).isEqualTo(expectedCause);
        assertThat(exception.getCause().getMessage()).isEqualTo("The root cause of the failure.");
    }
}
