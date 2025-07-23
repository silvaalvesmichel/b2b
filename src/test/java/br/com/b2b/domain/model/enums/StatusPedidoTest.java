package br.com.b2b.domain.model.enums;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the {@link StatusPedido} enum.
 */
@DisplayName("StatusPedido Enum Test")
class StatusPedidoTest {

    @Test
    @DisplayName("Should contain all expected enum constants in order")
    void values_shouldContainAllEnumConstants() {
        // when
        StatusPedido[] statuses = StatusPedido.values();

        // then
        assertThat(statuses).hasSize(6)
                .containsExactly(
                        StatusPedido.PENDENTE,
                        StatusPedido.APROVADO,
                        StatusPedido.EM_PROCESSAMENTO,
                        StatusPedido.ENVIADO,
                        StatusPedido.ENTREGUE,
                        StatusPedido.CANCELADO
                );
    }

    @ParameterizedTest
    @EnumSource(StatusPedido.class)
    @DisplayName("Should return correct enum for a valid string name")
    void valueOf_shouldReturnCorrectEnumForValidString(StatusPedido expectedStatus) {
        // when
        StatusPedido actualStatus = StatusPedido.valueOf(expectedStatus.name());

        // then
        assertThat(actualStatus).isEqualTo(expectedStatus);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for an invalid string name")
    void valueOf_shouldThrowIllegalArgumentExceptionForInvalidString() {
        // given
        String invalidStatusName = "INEXISTENTE";

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            StatusPedido.valueOf(invalidStatusName);
        });

        assertThat(exception.getMessage())
                .contains("No enum constant " + StatusPedido.class.getCanonicalName() + "." + invalidStatusName);
    }

    @Test
    @DisplayName("Should throw NullPointerException for a null input")
    void valueOf_shouldThrowNullPointerExceptionForNullInput() {
        // when & then
        // The Enum.valueOf(Class<T>, String) method, which is what the compiler uses here,
        // throws a NullPointerException if the name is null.
        assertThrows(NullPointerException.class, () -> {
            StatusPedido.valueOf(null);
        });
    }
}
