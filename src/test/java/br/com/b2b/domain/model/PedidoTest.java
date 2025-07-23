package br.com.b2b.domain.model;


import br.com.b2b.domain.exception.DomainException;
import br.com.b2b.domain.model.enums.StatusPedido;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para a classe Pedido")
class PedidoTest {

    private static final Long ID_PARCEIRO = 1L;

    @Mock
    private ItemPedido item1;

    @Mock
    private ItemPedido item2;

    @Nested
    @DisplayName("Criação de Pedido (newPedido)")
    class NewPedidoTests {

        @Test
        @DisplayName("Deve lançar NullPointerException para idParceiro nulo")
        void newPedido_deveLancarExcecaoParaIdParceiroNulo() {
            // Act & Assert
            assertThatThrownBy(() -> Pedido.newPedido(null, List.of(item1)))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("ID do parceiro é obrigatório.");
        }

    }

    @Nested
    @DisplayName("Atualização de Status (atualizarStatus)")
    class UpdateStatusTests {

        @Test
        @DisplayName("Deve retornar um novo pedido com o status atualizado")
        void atualizarStatus_deveRetornarNovoPedidoComStatusAtualizado() {
            // Arrange
            Pedido pedidoOriginal = Pedido.builder()
                    .id(UUID.randomUUID())
                    .idParceiro(ID_PARCEIRO)
                    .status(StatusPedido.PENDENTE)
                    .dataCriacao(LocalDateTime.now().minusDays(1))
                    .dataUltimaAtualizacao(LocalDateTime.now().minusDays(1))
                    .build();

            // Act
            Pedido pedidoAtualizado = Pedido.atualizarStatus(pedidoOriginal, StatusPedido.APROVADO);

            // Assert
            assertThat(pedidoAtualizado).isNotNull();
            assertThat(pedidoAtualizado.getId()).isEqualTo(pedidoOriginal.getId());
            assertThat(pedidoAtualizado.getStatus()).isEqualTo(StatusPedido.APROVADO);
            assertThat(pedidoAtualizado.getDataUltimaAtualizacao()).isAfter(pedidoOriginal.getDataUltimaAtualizacao());
            assertThat(pedidoAtualizado.getDataCriacao()).isEqualTo(pedidoOriginal.getDataCriacao());
        }
    }

    @Nested
    @DisplayName("Aprovação de Pedido (aprovar)")
    class AprovarTests {

        @Test
        @DisplayName("Deve lançar DomainException ao tentar aprovar um pedido não PENDENTE")
        void aprovar_deveLancarExcecaoParaPedidoNaoPendente() {
            // Arrange
            Pedido pedido = Pedido.builder().status(StatusPedido.CANCELADO).build();

            // Act & Assert
            assertThatThrownBy(pedido::aprovar)
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Apenas pedidos com status PENDENTE podem ser aprovados.");
        }
    }

    @Nested
    @DisplayName("Cancelamento de Pedido (cancelar)")
    class CancelarTests {

        @Test
        @DisplayName("Deve lançar DomainException ao tentar cancelar um pedido ENVIADO")
        void cancelar_deveLancarExcecaoParaPedidoEnviado() {
            // Arrange
            Pedido pedido = Pedido.builder().status(StatusPedido.ENVIADO).build();

            // Act & Assert
            assertThatThrownBy(pedido::cancelar)
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Não é possível cancelar um pedido que já foi enviado ou entregue.");
        }

        @Test
        @DisplayName("Deve lançar DomainException ao tentar cancelar um pedido ENTREGUE")
        void cancelar_deveLancarExcecaoParaPedidoEntregue() {
            // Arrange
            Pedido pedido = Pedido.builder().status(StatusPedido.ENTREGUE).build();

            // Act & Assert
            assertThatThrownBy(pedido::cancelar)
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Não é possível cancelar um pedido que já foi enviado ou entregue.");
        }

        @Test
        @DisplayName("Deve ser idempotente ao cancelar um pedido já CANCELADO")
        void cancelar_deveSerIdempotenteParaPedidoJaCancelado() {
            // Arrange
            Pedido pedido = Pedido.builder().status(StatusPedido.CANCELADO).build();
            LocalDateTime dataAtualizacaoOriginal = pedido.getDataUltimaAtualizacao();

            // Act & Assert
            assertDoesNotThrow(pedido::cancelar);
            assertThat(pedido.getStatus()).isEqualTo(StatusPedido.CANCELADO);
            assertThat(pedido.getDataUltimaAtualizacao()).isEqualTo(dataAtualizacaoOriginal); // Data não deve mudar
        }
    }

    @Nested
    @DisplayName("Cálculo de Valor (getValorTotalComoBigDecimal)")
    class ValorTotalTests {

        @Test
        @DisplayName("Deve retornar o valor total como BigDecimal")
        void getValorTotalComoBigDecimal_deveRetornarValorCorreto() {
            // Arrange
            ValorMonetario valorTotal = new ValorMonetario(new BigDecimal("250.75"));
            Pedido pedido = Pedido.builder().valorTotal(valorTotal).build();

            // Act
            BigDecimal resultado = pedido.getValorTotalComoBigDecimal();

            // Assert
            assertThat(resultado).isEqualByComparingTo("250.75");
        }

        @Test
        @DisplayName("Deve retornar nulo se o valor total for nulo")
        void getValorTotalComoBigDecimal_deveRetornarNuloSeValorTotalNulo() {
            // Arrange
            Pedido pedido = Pedido.builder().valorTotal(null).build();

            // Act
            BigDecimal resultado = pedido.getValorTotalComoBigDecimal();

            // Assert
            assertThat(resultado).isNull();
        }
    }
}
