package br.com.b2b.infrastructure.creditoparceiro.mapper;


import br.com.b2b.domain.model.CreditoParceiro;
import br.com.b2b.infrastructure.openapi.credito.parceiro.model.ParceiroCreditoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreditoParceiroMapperTest {

    private CreditoParceiroMapper creditoParceiroMapper;

    @BeforeEach
    void setUp() {
        // Since the class is abstract but the method is concrete,
        // we can instantiate it with an anonymous inner class for testing.
        creditoParceiroMapper = new CreditoParceiroMapper() {};
    }

    @Test
    @DisplayName("Should correctly map CreditoParceiro to ParceiroCreditoResponse")
    void shouldMapCreditoParceiroToResponse() {
        // Arrange
        CreditoParceiro domainModel = CreditoParceiro.builder()
                .id(UUID.randomUUID())
                .idParceiro(12345L)
                .credito(new BigDecimal("2500.50"))
                .build();

        // Act
        ParceiroCreditoResponse response = creditoParceiroMapper.toResponse(domainModel);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(domainModel.getId());
        assertThat(response.getIdParceiro()).isEqualTo(domainModel.getIdParceiro());
        assertThat(response.getCredito()).isEqualTo(domainModel.getCredito());
    }

    @Test
    @DisplayName("Should throw NullPointerException when CreditoParceiro is null")
    void shouldThrowExceptionWhenInputIsNull() {
        // Arrange
        CreditoParceiro domainModel = null;

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            creditoParceiroMapper.toResponse(domainModel);
        }, "Mapping a null object should throw a NullPointerException");
    }
}
