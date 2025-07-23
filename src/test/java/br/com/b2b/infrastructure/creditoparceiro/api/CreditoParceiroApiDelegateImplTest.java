package br.com.b2b.infrastructure.creditoparceiro.api;


import br.com.b2b.application.port.in.CreditoParceiroUseCase;
import br.com.b2b.domain.model.CreditoParceiro;
import br.com.b2b.infrastructure.creditoparceiro.mapper.CreditoParceiroMapper;
import br.com.b2b.infrastructure.openapi.credito.parceiro.model.ParceiroCreditoRequest;
import br.com.b2b.infrastructure.openapi.credito.parceiro.model.ParceiroCreditoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link CreditoParceiroApiDelegateImpl}.
 */
@ExtendWith(MockitoExtension.class)
class CreditoParceiroApiDelegateImplTest {

    @Mock
    private CreditoParceiroUseCase useCase;

    @Mock
    private CreditoParceiroMapper mapper;

    @InjectMocks
    private CreditoParceiroApiDelegateImpl delegate;

    private ParceiroCreditoRequest request;
    private CreditoParceiro creditoParceiroDomain;
    private ParceiroCreditoResponse parceiroCreditoResponse;

    @BeforeEach
    void setUp() {
        // Common setup for tests
        request = new ParceiroCreditoRequest();
        request.setIdParceiro(1L);
        request.setCredito(new BigDecimal("1500.75"));

        creditoParceiroDomain = CreditoParceiro.builder()
                .id(UUID.randomUUID())
                .idParceiro(request.getIdParceiro())
                .credito(request.getCredito())
                .build();

        parceiroCreditoResponse = new ParceiroCreditoResponse();
        parceiroCreditoResponse.setId(creditoParceiroDomain.getId());
        parceiroCreditoResponse.setIdParceiro(creditoParceiroDomain.getIdParceiro());
        parceiroCreditoResponse.setCredito(creditoParceiroDomain.getCredito());
    }

    @Test
    @DisplayName("Should create partner credit and return 200 OK on success")
    void criar_shouldReturnOk_whenSuccessful() throws Exception {
        // Arrange: Mock the behavior of dependencies
        when(useCase.criarCreditoParceiro(request.getIdParceiro(), request.getCredito()))
                .thenReturn(creditoParceiroDomain);
        when(mapper.toResponse(creditoParceiroDomain)).thenReturn(parceiroCreditoResponse);

        // Act: Call the method under test
        ResponseEntity<ParceiroCreditoResponse> result = delegate.criar(request);

        // Assert: Verify the results
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(parceiroCreditoResponse, result.getBody());

        // Verify that the use case and mapper were called with the correct parameters
        verify(useCase).criarCreditoParceiro(request.getIdParceiro(), request.getCredito());
        verify(mapper).toResponse(creditoParceiroDomain);
    }

    @Test
    @DisplayName("Should propagate exception when use case throws an error")
    void criar_shouldThrowException_whenUseCaseFails() throws Exception {
        // Arrange: Mock the use case to throw an exception
        when(useCase.criarCreditoParceiro(any(Long.class), any(BigDecimal.class)))
                .thenThrow(new IllegalStateException("Business rule violation"));

        // Act & Assert: Verify that the exception is thrown by the delegate
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            delegate.criar(request);
        });

        assertEquals("Business rule violation", exception.getMessage());

        // Verify that the use case was still called
        verify(useCase).criarCreditoParceiro(request.getIdParceiro(), request.getCredito());
    }
}
