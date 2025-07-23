package br.com.b2b.application.service;


import br.com.b2b.application.port.out.ParceiroCreditoRepositoryPort;
import br.com.b2b.domain.exception.DomainException;
import br.com.b2b.domain.model.CreditoParceiro;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditoParceiroServiceImplTest {

    @Mock
    private ParceiroCreditoRepositoryPort parceiroCreditoRepositoryPort;

    @InjectMocks
    private CreditoParceiroServiceImpl creditoParceiroService;

    @Test
    @DisplayName("Deve.domain.model.CreditoParceiro; criar crédito para um novo parceiro com sucesso")
    void deveCriarCreditoParceiroComSucesso() {
        // Arrange
        Long idParceiro = 1L;
        BigDecimal creditoInicial = new BigDecimal("1000.00");

        // Mocking the behavior for a non-existent partner
        when(parceiroCreditoRepositoryPort.buscarPorIdParceiro(idParceiro)).thenReturn(null);

        // Mocking the creation behavior
        CreditoParceiro creditoParceiroCriado = CreditoParceiro.builder()
                .id(UUID.randomUUID())
                .idParceiro(idParceiro)
                .credito(creditoInicial)
                .build();
        when(parceiroCreditoRepositoryPort.criarParceiroCredito(any(CreditoParceiro.class))).thenReturn(creditoParceiroCriado);

        // Act
        CreditoParceiro result = creditoParceiroService.criarCreditoParceiro(idParceiro, creditoInicial);

        // Assert
        assertNotNull(result);
        assertEquals(idParceiro, result.getIdParceiro());
        assertEquals(0, creditoInicial.compareTo(result.getCredito()));

        // Verify interactions
        verify(parceiroCreditoRepositoryPort).buscarPorIdParceiro(idParceiro);
        verify(parceiroCreditoRepositoryPort).criarParceiroCredito(any(CreditoParceiro.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar crédito para um parceiro já existente")
    void deveLancarExcecaoAoTentarCriarCreditoParaParceiroExistente() {
        // Arrange
        Long idParceiro = 2L;
        BigDecimal credito = new BigDecimal("500.00");
        CreditoParceiro creditoParceiroExistente = CreditoParceiro.builder()
                .id(UUID.randomUUID())
                .idParceiro(idParceiro)
                .credito(new BigDecimal("2000.00"))
                .build();

        // Mocking the behavior for an existing partner
        when(parceiroCreditoRepositoryPort.buscarPorIdParceiro(idParceiro)).thenReturn(creditoParceiroExistente);

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class, () -> {
            creditoParceiroService.criarCreditoParceiro(idParceiro, credito);
        });

        assertEquals("Parceiro já cadastrado", exception.getMessage());

        // Verify interactions
        verify(parceiroCreditoRepositoryPort).buscarPorIdParceiro(idParceiro);
        verify(parceiroCreditoRepositoryPort, never()).criarParceiroCredito(any(CreditoParceiro.class));
    }
}
