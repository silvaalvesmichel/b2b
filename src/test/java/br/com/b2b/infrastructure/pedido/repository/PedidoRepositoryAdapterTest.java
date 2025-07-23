package br.com.b2b.infrastructure.pedido.repository;


import br.com.b2b.domain.model.Pedido;
import br.com.b2b.domain.model.ValorMonetario;
import br.com.b2b.domain.model.enums.StatusPedido;
import br.com.b2b.infrastructure.commons.Pagination;
import br.com.b2b.infrastructure.pedido.entity.PedidoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoRepositoryAdapterTest {

    @Mock
    private PedidoRepository jpaRepository;

    @InjectMocks
    private PedidoRepositoryAdapter pedidoRepositoryAdapter;

    private Pedido pedido;
    private PedidoEntity pedidoEntity;
    private UUID pedidoId;

    @BeforeEach
    void setUp() {
        pedidoId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        // This setup assumes a mapper correctly converts between Pedido and PedidoEntity.
        // For this test, we create both objects manually to represent the state
        // before and after the repository interaction.
        pedido = Pedido.builder()
                .id(pedidoId)
                .idParceiro(1L)
                .status(StatusPedido.PENDENTE)
                .dataCriacao(now)
                .dataUltimaAtualizacao(now)
                .itens(Collections.emptyList())
                .valorTotal(new ValorMonetario(new BigDecimal("199.99")))
                .build();

        pedidoEntity = PedidoEntity.builder()
                .id(pedidoId)
                .idParceiro(1L)
                .status(StatusPedido.PENDENTE)
                .dataCriacao(now)
                .dataUltimaAtualizacao(now)
                .itens(Collections.emptyList())
                .valorTotal(new BigDecimal("199.99"))
                .build();
    }

    @Test
    @DisplayName("Should save a Pedido successfully")
    void salvar_deveSalvarPedido_quandoPedidoValido() {
        // Arrange
        // We assume PedidoEntity.from() works correctly.
        // The mock ensures the repository returns a valid entity after saving.
        when(jpaRepository.save(any(PedidoEntity.class))).thenReturn(pedidoEntity);

        // Act
        Pedido pedidoSalvo = pedidoRepositoryAdapter.salvar(pedido);

        // Assert
        assertNotNull(pedidoSalvo);
        assertEquals(pedido.getId(), pedidoSalvo.getId());
        assertEquals(pedido.getStatus(), pedidoSalvo.getStatus());
        assertEquals(pedido.getIdParceiro(), pedidoSalvo.getIdParceiro());
        assertEquals(0, pedido.getValorTotal().valor().compareTo(pedidoSalvo.getValorTotal().valor()));

        // Capture the argument passed to the repository to verify the mapping
        ArgumentCaptor<PedidoEntity> pedidoEntityCaptor = ArgumentCaptor.forClass(PedidoEntity.class);
        verify(jpaRepository).save(pedidoEntityCaptor.capture());
        PedidoEntity capturedEntity = pedidoEntityCaptor.getValue();

        assertEquals(pedido.getId(), capturedEntity.getId());
        assertEquals(pedido.getIdParceiro(), capturedEntity.getIdParceiro());
    }

    @Test
    @DisplayName("Should return a Pedido when ID exists")
    void buscarPorId_deveRetornarPedido_quandoIdExistir() {
        // Arrange
        when(jpaRepository.findById(pedidoId)).thenReturn(Optional.of(pedidoEntity));

        // Act
        Optional<Pedido> resultado = pedidoRepositoryAdapter.buscarPorId(pedidoId);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(pedidoId, resultado.get().getId());
        verify(jpaRepository).findById(pedidoId);
    }

    @Test
    @DisplayName("Should return an empty Optional when ID does not exist")
    void buscarPorId_deveRetornarOptionalVazio_quandoIdNaoExistir() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        when(jpaRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act
        Optional<Pedido> resultado = pedidoRepositoryAdapter.buscarPorId(idInexistente);

        // Assert
        assertFalse(resultado.isPresent());
        verify(jpaRepository).findById(idInexistente);
    }

    @Test
    @DisplayName("Should list paginated pedidos without any filters")
    void listarPedidos_deveRetornarPaginacao_semFiltros() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<PedidoEntity> pedidoEntityList = List.of(pedidoEntity);
        Page<PedidoEntity> pageResult = new PageImpl<>(pedidoEntityList, pageRequest, 1);

        // We verify that findAll is called with *any* specification, as testing the
        // specification's internal logic is better suited for an integration test.
        when(jpaRepository.findAll(any(Specification.class), eq(pageRequest))).thenReturn(pageResult);

        // Act
        Pagination<Pedido> resultado = pedidoRepositoryAdapter.listarPedidos(pageRequest, null, null, null, null);

        // Assert
        assertNotNull(resultado);
        assertEquals(0, resultado.currentPage());
        assertEquals(10, resultado.perPage());
        assertEquals(1, resultado.total());
        assertEquals(1, resultado.items().size());
        assertEquals(pedidoId, resultado.items().get(0).getId());

        verify(jpaRepository).findAll(any(Specification.class), eq(pageRequest));
    }

    @Test
    @DisplayName("Should list paginated pedidos with all filters applied")
    void listarPedidos_deveRetornarPaginacao_comTodosFiltros() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 5);
        List<PedidoEntity> pedidoEntityList = List.of(pedidoEntity);
        Page<PedidoEntity> pageResult = new PageImpl<>(pedidoEntityList, pageRequest, 1);
        String status = "PENDENTE";
        OffsetDateTime dateStart = OffsetDateTime.now(ZoneOffset.UTC).minusDays(1);
        OffsetDateTime dateEnd = OffsetDateTime.now(ZoneOffset.UTC).plusDays(1);

        when(jpaRepository.findAll(any(Specification.class), eq(pageRequest))).thenReturn(pageResult);

        // Act
        Pagination<Pedido> resultado = pedidoRepositoryAdapter.listarPedidos(pageRequest, pedidoId, status, dateStart, dateEnd);

        // Assert
        assertNotNull(resultado);
        assertEquals(0, resultado.currentPage());
        assertEquals(5, resultado.perPage());
        assertEquals(1, resultado.total());
        assertEquals(1, resultado.items().size());
        assertEquals(pedidoId, resultado.items().get(0).getId());

        verify(jpaRepository).findAll(any(Specification.class), eq(pageRequest));
    }

    @Test
    @DisplayName("Should return an empty pagination when no pedidos are found")
    void listarPedidos_deveRetornarPaginacaoVazia_quandoNaoEncontrarPedidos() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<PedidoEntity> emptyPage = new PageImpl<>(Collections.emptyList(), pageRequest, 0);

        when(jpaRepository.findAll(any(Specification.class), eq(pageRequest))).thenReturn(emptyPage);

        // Act
        Pagination<Pedido> resultado = pedidoRepositoryAdapter.listarPedidos(pageRequest, null, null, null, null);

        // Assert
        assertNotNull(resultado);
        assertEquals(0, resultado.currentPage());
        assertEquals(10, resultado.perPage());
        assertEquals(0, resultado.total());
        assertTrue(resultado.items().isEmpty());

        verify(jpaRepository).findAll(any(Specification.class), eq(pageRequest));
    }
}