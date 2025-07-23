package br.com.b2b.infrastructure.commons.exception;


import br.com.b2b.domain.exception.DomainException;
import br.com.b2b.infrastructure.commons.exeception.InfrastructureException;
import br.com.b2b.infrastructure.commons.exeception.RestExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("RestExceptionHandler Tests")
class RestExceptionHandlerTest {

    private RestExceptionHandler restExceptionHandler;

    @BeforeEach
    void setUp() {
        restExceptionHandler = new RestExceptionHandler();
    }

    @Test
    @DisplayName("Should handle DomainException and return 422 Unprocessable Entity")
    void handleDomainException_shouldReturnUnprocessableEntity() {
        // Arrange
        final String errorMessage = "A specific domain rule was violated.";
        DomainException ex = new DomainException(errorMessage);

        // Act
        ResponseEntity<Map<String, String>> responseEntity = restExceptionHandler.handleDomainException(ex);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().get("erro")).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("Should handle InfrastructureException and return 422 Unprocessable Entity")
    void handleInfrastructureException_shouldReturnUnprocessableEntity() {
        // Arrange
        final String errorMessage = "A specific infrastructure operation failed.";
        InfrastructureException ex = new InfrastructureException(errorMessage);

        // Act
        ResponseEntity<Map<String, String>> responseEntity = restExceptionHandler.handleInfrastructureException(ex);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().get("erro")).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException and return 400 Bad Request")
    void handleValidationException_shouldReturnBadRequest() {
        // Arrange
        // We need to create an instance of MethodArgumentNotValidException, which requires mocking some Spring classes.
        MethodParameter parameter = mock(MethodParameter.class);
        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        // Act
        ResponseEntity<Map<String, String>> responseEntity = restExceptionHandler.handleValidationException(ex);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().get("erro")).isEqualTo("Dados de entrada inválidos.");
    }
}
