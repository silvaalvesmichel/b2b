package br.com.b2b.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValorMonetarioTest {

    @Test
    @DisplayName("Deve criar ValorMonetario com sucesso para valor positivo")
    void shouldCreateSuccessfullyForPositiveValue() {
        // Given
        BigDecimal valorPositivo = new BigDecimal("100.50");

        // When
        ValorMonetario valorMonetario = new ValorMonetario(valorPositivo);

        // Then
        assertThat(valorMonetario).isNotNull();
        assertThat(valorMonetario.valor()).isEqualByComparingTo(valorPositivo);
    }

    @Test
    @DisplayName("Deve criar ValorMonetario com sucesso para valor zero")
    void shouldCreateSuccessfullyForZeroValue() {
        // Given
        BigDecimal valorZero = BigDecimal.ZERO;

        // When
        ValorMonetario valorMonetario = new ValorMonetario(valorZero);

        // Then
        assertThat(valorMonetario).isNotNull();
        assertThat(valorMonetario.valor()).isEqualByComparingTo(valorZero);
    }

    @Test
    @DisplayName("Deve lançar NullPointerException ao tentar criar com valor nulo")
    void shouldThrowNullPointerExceptionWhenValueIsNull() {
        // Given
        BigDecimal valorNulo = null;

        // When & Then
        assertThatThrownBy(() -> new ValorMonetario(valorNulo))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Valor não pode ser nulo");
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException ao tentar criar com valor negativo")
    void shouldThrowIllegalArgumentExceptionWhenValueIsNegative() {
        // Given
        BigDecimal valorNegativo = new BigDecimal("-10.00");

        // When & Then
        assertThatThrownBy(() -> new ValorMonetario(valorNegativo))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Valor monetário não pode ser negativo.");
    }

    @Test
    @DisplayName("Deve adicionar dois valores monetários corretamente")
    void shouldAddTwoMonetaryValuesCorrectly() {
        // Given
        ValorMonetario valor1 = new ValorMonetario(new BigDecimal("150.75"));
        ValorMonetario valor2 = new ValorMonetario(new BigDecimal("49.25"));

        // When
        ValorMonetario resultado = valor1.adicionar(valor2);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.valor()).isEqualByComparingTo(new BigDecimal("200.00"));
    }

    @Test
    @DisplayName("Deve retornar um novo objeto ao adicionar, mantendo a imutabilidade")
    void shouldReturnNewInstanceWhenAdding() {
        // Given
        ValorMonetario valorOriginal = new ValorMonetario(new BigDecimal("100"));
        ValorMonetario valorParaAdicionar = new ValorMonetario(new BigDecimal("50"));

        // When
        ValorMonetario resultado = valorOriginal.adicionar(valorParaAdicionar);

        // Then
        assertThat(resultado).isNotSameAs(valorOriginal);
        assertThat(resultado).isNotSameAs(valorParaAdicionar);

        // Verify original objects were not mutated
        assertThat(valorOriginal.valor()).isEqualByComparingTo(new BigDecimal("100"));
        assertThat(valorParaAdicionar.valor()).isEqualByComparingTo(new BigDecimal("50"));
    }
}
