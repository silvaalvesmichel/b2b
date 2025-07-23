package br.com.b2b.infrastructure.pedido.api;


import br.com.b2b.application.port.in.PedidoUseCase;
import br.com.b2b.domain.model.ItemPedido;
import br.com.b2b.domain.model.Pedido;
import br.com.b2b.infrastructure.commons.Pagination;
import br.com.b2b.infrastructure.openapi.pedido.model.PedidoPaginadoResponse;
import br.com.b2b.infrastructure.openapi.pedido.model.PedidoRequest;
import br.com.b2b.infrastructure.openapi.pedido.model.PedidoResponse;
import br.com.b2b.infrastructure.pedido.mapper.PedidoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PedidoApiDelegateImpl Tests")
class PedidoApiDelegateImplTest {

    @Mock
    private PedidoUseCase useCase;

    @Mock
    private PedidoMapper mapper;

    @InjectMocks
    private PedidoApiDelegateImpl delegate;

    private PedidoRequest pedidoRequest;
    private Pedido pedido;
    private PedidoResponse pedidoResponse;

    @BeforeEach
    void setUp() {
        // Common test data setup
        pedidoRequest = new PedidoRequest();
        pedidoRequest.setIdParceiro(1L);
        pedidoRequest.setItens(Collections.singletonList(new br.com.b2b.infrastructure.openapi.pedido.model.ItemPedido()));

        pedido = Pedido.builder().id(UUID.randomUUID()).build();
        pedidoResponse = new PedidoResponse();
        pedidoResponse.setId(pedido.getId());
    }

    @Test
    @DisplayName("Should create a new pedido successfully")
    void criar_shouldReturnOk_whenPedidoIsCreated() throws Exception {
        // Arrange
        List<ItemPedido> domainItens = Collections.singletonList(ItemPedido.builder().build());
        when(mapper.toItensDomain(pedidoRequest.getItens())).thenReturn(domainItens);
        when(useCase.criarPedido(pedidoRequest.getIdParceiro(), domainItens)).thenReturn(pedido);
        when(mapper.toResponse(pedido)).thenReturn(pedidoResponse);

        // Act
        ResponseEntity<PedidoResponse> response = delegate.criar(pedidoRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pedidoResponse, response.getBody());

        verify(mapper).toItensDomain(pedidoRequest.getItens());
        verify(useCase).criarPedido(1L, domainItens);
        verify(mapper).toResponse(pedido);
    }

    @Test
    @DisplayName("Should update pedido status successfully")
    void atualizarStatus_shouldReturnOk_whenStatusIsUpdated() {
        // Arrange
        UUID pedidoId = UUID.randomUUID();
        String newStatus = "APROVADO";
        when(useCase.atualizarStatus(pedidoId, newStatus)).thenReturn(pedido);
        when(mapper.toResponse(pedido)).thenReturn(pedidoResponse);

        // Act
        ResponseEntity<PedidoResponse> response = delegate.atualizarStatus(pedidoId, newStatus);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pedidoResponse, response.getBody());

        verify(useCase).atualizarStatus(pedidoId, newStatus);
        verify(mapper).toResponse(pedido);
    }

    @Test
    @DisplayName("Should list pedidos with pagination successfully")
    void listar_shouldReturnOk_withPaginatedPedidos() throws Exception {
        // Arrange
        String search = "test";
        Integer page = 0;
        Integer perPage = 10;
        String dir = "asc";
        String sort = "dataCriacao";
        UUID id = UUID.randomUUID();
        String status = "PENDENTE";
        OffsetDateTime dateStart = OffsetDateTime.now().minusDays(1);
        OffsetDateTime dateEnd = OffsetDateTime.now();

        Pagination<Pedido> paginatedPedidos = new Pagination<>(page, perPage, 1L, Collections.singletonList(pedido));
        PedidoPaginadoResponse paginatedResponse = new PedidoPaginadoResponse();

        when(useCase.listarPedidos(search, page, perPage, dir, sort, id, status, dateStart, dateEnd))
                .thenReturn(paginatedPedidos);
        when(mapper.toResponsePagination(paginatedPedidos)).thenReturn(paginatedResponse);

        // Act
        ResponseEntity<PedidoPaginadoResponse> response = delegate.listar(search, page, perPage, dir, sort, id, status, dateStart, dateEnd);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(paginatedResponse, response.getBody());

        verify(useCase).listarPedidos(search, page, perPage, dir, sort, id, status, dateStart, dateEnd);
        verify(mapper).toResponsePagination(paginatedPedidos);
    }
}
