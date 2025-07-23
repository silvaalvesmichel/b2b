package br.com.b2b.application.service;

import br.com.b2b.application.port.out.NotificacaoPort;
import br.com.b2b.application.port.out.ParceiroCreditoRepositoryPort;
import br.com.b2b.application.port.out.PedidoRepositoryPort;
import br.com.b2b.domain.exception.DomainException;
import br.com.b2b.domain.model.ItemPedido;
import br.com.b2b.domain.model.Pedido;
import br.com.b2b.domain.model.ValorMonetario;
import br.com.b2b.domain.model.enums.StatusPedido;
import br.com.b2b.infrastructure.commons.Pagination;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceImplTest {

    @Mock
    private PedidoRepositoryPort pedidoRepository;

    @Mock
    private ParceiroCreditoRepositoryPort parceiroCreditoRepositoryPort;

    @Mock
    private NotificacaoPort notificacaoPort;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    private Pedido pedido;
    private List<ItemPedido> itens;

    @BeforeEach
    void setUp() {
        itens = List.of(
                ItemPedido.builder().produto("Produto A").quantidade(2).precoUnitario(new ValorMonetario(new BigDecimal("10.00"))).build()
        );

        pedido = Pedido.builder()
                .id(UUID.randomUUID())
                .idParceiro(1L)
                .itens(itens)
                .valorTotal(new ValorMonetario(new BigDecimal("20.00")))
                .status(StatusPedido.PENDENTE)
                .dataCriacao(LocalDateTime.now())
                .dataUltimaAtualizacao(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Testes para criarPedido")
    class CriarPedidoTests {

        @Test
        @DisplayName("Deve criar um pedido com sucesso quando o parceiro tiver crédito")
        void criarPedido_shouldCreateOrder_whenPartnerHasCredit() throws JsonProcessingException {
            // Arrange
            when(parceiroCreditoRepositoryPort.verificarCredito(anyLong(), any(BigDecimal.class))).thenReturn(true);
            when(pedidoRepository.salvar(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            Pedido pedidoCriado = pedidoService.criarPedido(1L, itens);

            // Assert
            assertThat(pedidoCriado).isNotNull();
            assertThat(pedidoCriado.getStatus()).isEqualTo(StatusPedido.PENDENTE);

            verify(parceiroCreditoRepositoryPort).verificarCredito(1L, new BigDecimal("20.00"));
            verify(pedidoRepository).salvar(any(Pedido.class));
            verify(parceiroCreditoRepositoryPort).debitarCredito(1L, new BigDecimal("20.00"));
            verify(notificacaoPort).notificarMudancaStatus(pedidoCriado);
        }

        @Test
        @DisplayName("Deve lançar DomainException quando o parceiro não tiver crédito")
        void criarPedido_shouldThrowDomainException_whenPartnerHasNoCredit() {
            // Arrange
            when(parceiroCreditoRepositoryPort.verificarCredito(anyLong(), any(BigDecimal.class))).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> pedidoService.criarPedido(1L, itens))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Parceiro não possui crédito suficiente");

            verify(pedidoRepository, never()).salvar(any(Pedido.class));
            verify(parceiroCreditoRepositoryPort, never()).debitarCredito(anyLong(), any(BigDecimal.class));
            verifyNoInteractions(notificacaoPort);
        }

        @Test
        @DisplayName("Deve criar pedido mesmo se a notificação falhar")
        void criarPedido_shouldCreateOrder_whenNotificationFails() throws JsonProcessingException {
            // Arrange
            when(parceiroCreditoRepositoryPort.verificarCredito(anyLong(), any(BigDecimal.class))).thenReturn(true);
            when(pedidoRepository.salvar(any(Pedido.class))).thenReturn(pedido);
            doThrow(new JsonProcessingException("Erro de notificação") {}).when(notificacaoPort).notificarMudancaStatus(any(Pedido.class));

            // Act
            Pedido pedidoCriado = pedidoService.criarPedido(1L, itens);

            // Assert
            assertThat(pedidoCriado).isNotNull();
            verify(parceiroCreditoRepositoryPort).verificarCredito(anyLong(), any(BigDecimal.class));
            verify(pedidoRepository).salvar(any(Pedido.class));
            verify(parceiroCreditoRepositoryPort).debitarCredito(anyLong(), any(BigDecimal.class));
            verify(notificacaoPort).notificarMudancaStatus(any(Pedido.class));
        }
    }

    @Nested
    @DisplayName("Testes para atualizarStatus")
    class AtualizarStatusTests {

        @Test
        @DisplayName("Deve atualizar o status de um pedido com sucesso")
        void atualizarStatus_shouldUpdateStatusSuccessfully() throws JsonProcessingException {
            // Arrange
            when(pedidoRepository.buscarPorId(pedido.getId())).thenReturn(Optional.of(pedido));
            when(pedidoRepository.salvar(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));
            String novoStatus = "EM_PROCESSAMENTO";

            // Act
            Pedido pedidoAtualizado = pedidoService.atualizarStatus(pedido.getId(), novoStatus);

            // Assert
            assertThat(pedidoAtualizado).isNotNull();
            assertThat(pedidoAtualizado.getStatus()).isEqualTo(StatusPedido.EM_PROCESSAMENTO);
            verify(pedidoRepository).salvar(any(Pedido.class));
            verify(notificacaoPort).notificarMudancaStatus(pedidoAtualizado);
        }

        @Test
        @DisplayName("Deve lançar DomainException ao tentar atualizar status de um pedido não encontrado")
        void atualizarStatus_shouldThrowDomainException_whenOrderNotFound() {
            // Arrange
            UUID idInexistente = UUID.randomUUID();
            when(pedidoRepository.buscarPorId(idInexistente)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> pedidoService.atualizarStatus(idInexistente, "APROVADO"))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Pedido não encontrado.");
        }

        @Test
        @DisplayName("Deve lançar DomainException ao usar um status inválido")
        void atualizarStatus_shouldThrowDomainException_whenStatusIsInvalid() {
            // Arrange
            when(pedidoRepository.buscarPorId(pedido.getId())).thenReturn(Optional.of(pedido));
            String statusInvalido = "STATUS_INVALIDO";

            // Act & Assert
            assertThatThrownBy(() -> pedidoService.atualizarStatus(pedido.getId(), statusInvalido))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Status inválido.");
        }
    }

    @Nested
    @DisplayName("Testes para listarPedidos")
    class ListarPedidosTests {

        @Test
        @DisplayName("Deve listar pedidos com sucesso")
        void listarPedidos_shouldListOrdersSuccessfully() {
            // Arrange
            PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "dataCriacao"));
            Pagination<Pedido> paginacaoEsperada = new Pagination<>(0, 10, 1, List.of(pedido));
            when(pedidoRepository.listarPedidos(any(PageRequest.class), any(), any(), any(), any())).thenReturn(paginacaoEsperada);

            // Act
            Pagination<Pedido> resultado = pedidoService.listarPedidos(
                    null, 0, 10, "asc", "dataCriacao", null, "PENDENTE", null, null
            );

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.items()).hasSize(1);
            assertThat(resultado).isEqualTo(paginacaoEsperada);
            verify(pedidoRepository).listarPedidos(pageRequest, null, "PENDENTE", null, null);
        }

        @Test
        @DisplayName("Deve lançar DomainException para status inválido quando 'sort' não é nulo")
        void listarPedidos_shouldThrowDomainException_forInvalidStatusWhenSortIsNotNull() {
            // Act & Assert
            assertThatThrownBy(() -> pedidoService.listarPedidos(
                    null, 0, 10, "asc", "dataCriacao", null, "STATUS_INVALIDO", null, null
            )).isInstanceOf(DomainException.class).hasMessage("Status inválido.");

            verify(pedidoRepository, never()).listarPedidos(any(), any(), any(), any(), any());
        }

    }
}
