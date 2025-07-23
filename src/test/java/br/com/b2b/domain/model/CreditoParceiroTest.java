package br.com.b2b.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreditoParceiroTest {

    @Test
    @DisplayName("Should create a new CreditoParceiro with a random UUID")
    void newCreditoParceiro_shouldCreateInstanceWithRandomUUID() {
        // Arrange
        Long idParceiro = 1L;
        BigDecimal credito = new BigDecimal("100.50");

        // Act
        CreditoParceiro creditoParceiro = CreditoParceiro.newCreditoParceiro(idParceiro, credito);

        // Assert
        assertThat(creditoParceiro).isNotNull();
        assertThat(creditoParceiro.getId()).isNotNull();
        assertThat(creditoParceiro.getIdParceiro()).isEqualTo(idParceiro);
        assertThat(creditoParceiro.getCredito()).isEqualByComparingTo(credito);
    }

    @Test
    @DisplayName("Should update an existing CreditoParceiro with a new credit value")
    void atualizarCredito_shouldUpdateCreditValue() {
        // Arrange
        CreditoParceiro originalCreditoParceiro = CreditoParceiro.builder()
                .id(UUID.randomUUID())
                .idParceiro(2L)
                .credito(new BigDecimal("200.00"))
                .build();

        BigDecimal novoCredito = new BigDecimal("500.75");

        // Act
        CreditoParceiro creditoAtualizado = CreditoParceiro.atualizarCredito(originalCreditoParceiro, novoCredito);

        // Assert
        assertThat(creditoAtualizado).isNotNull();
        assertThat(creditoAtualizado).isNotSameAs(originalCreditoParceiro); // Ensure it's a new instance
        assertThat(creditoAtualizado.getId()).isEqualTo(originalCreditoParceiro.getId());
        assertThat(creditoAtualizado.getIdParceiro()).isEqualTo(originalCreditoParceiro.getIdParceiro());
        assertThat(creditoAtualizado.getCredito()).isEqualByComparingTo(novoCredito);
    }

    @Test
    @DisplayName("Should correctly build a CreditoParceiro instance using the builder")
    void builder_shouldCreateInstanceWithAllFields() {
        // Arrange
        UUID id = UUID.randomUUID();
        Long idParceiro = 3L;
        BigDecimal credito = new BigDecimal("1234.56");

        // Act
        CreditoParceiro creditoParceiro = CreditoParceiro.builder()
                .id(id)
                .idParceiro(idParceiro)
                .credito(credito)
                .build();

        // Assert
        assertThat(creditoParceiro.getId()).isEqualTo(id);
        assertThat(creditoParceiro.getIdParceiro()).isEqualTo(idParceiro);
        assertThat(creditoParceiro.getCredito()).isEqualByComparingTo(credito);
    }

    @Test
    @DisplayName("Should throw NullPointerException when updating a null CreditoParceiro")
    void atualizarCredito_whenGivenNullCreditoParceiro_shouldThrowException() {
        // Arrange
        BigDecimal novoCredito = new BigDecimal("100");

        // Act & Assert
        assertThatThrownBy(() -> CreditoParceiro.atualizarCredito(null, novoCredito))
                .isInstanceOf(NullPointerException.class);
    }
}
