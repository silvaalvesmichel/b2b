package br.com.b2b.infrastructure.pedido.mapper;

import br.com.b2b.domain.model.ItemPedido;
import br.com.b2b.domain.model.Pedido;
import br.com.b2b.domain.model.ValorMonetario;
import br.com.b2b.domain.model.enums.StatusPedido;
import br.com.b2b.infrastructure.commons.Pagination;
import br.com.b2b.infrastructure.openapi.pedido.model.ItemResponse;
import br.com.b2b.infrastructure.openapi.pedido.model.PedidoPaginadoResponse;
import br.com.b2b.infrastructure.openapi.pedido.model.PedidoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PedidoMapper Tests")
class PedidoMapperTest {

    private PedidoMapper pedidoMapper;

    @BeforeEach
    void setUp() {
        // Use MapStruct's factory to get an instance of the mapper implementation
        pedidoMapper = Mappers.getMapper(PedidoMapper.class);
    }

    @Test
    @DisplayName("should correctly map Pedido domain to PedidoResponse")
    void toResponse_shouldMapPedidoToPedidoResponse() {
        // given
        Pedido pedido = createTestPedido();

        // when
        PedidoResponse response = pedidoMapper.toResponse(pedido);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(pedido.getId());
        assertThat(response.getIdParceiro()).isEqualTo(pedido.getIdParceiro());
        assertThat(response.getValorTotal()).isEqualTo(pedido.getValorTotalComoBigDecimal());
        assertThat(response.getStatus().name()).isEqualTo(pedido.getStatus().name());
        assertThat(response.getDataCriacao()).isEqualTo(pedido.getDataCriacao().atOffset(ZoneOffset.UTC));
        assertThat(response.getDataUltimaAtualizacao()).isEqualTo(pedido.getDataUltimaAtualizacao().atOffset(ZoneOffset.UTC));
        assertThat(response.getItens()).hasSize(2);
        assertThat(response.getItens().get(0).getProduto()).isEqualTo("Produto A");
    }

    @ParameterizedTest
    @EnumSource(StatusPedido.class)
    @DisplayName("should map all StatusPedido enum values to PedidoResponse.StatusEnum")
    void mapStatus_shouldMapAllStatusValues(StatusPedido status) {
        // when
        PedidoResponse.StatusEnum responseStatus = pedidoMapper.mapStatus(status);

        // then
        assertThat(responseStatus.name()).isEqualTo(status.name());
    }

    @Test
    @DisplayName("should map LocalDateTime to OffsetDateTime with UTC zone")
    void mapDateTime_shouldConvertToOffsetDateTimeWithUTC() {
        // given
        LocalDateTime localDateTime = LocalDateTime.of(2023, 10, 27, 10, 30, 0);

        // when
        OffsetDateTime offsetDateTime = pedidoMapper.mapDateTime(localDateTime);

        // then
        assertThat(offsetDateTime.toLocalDateTime()).isEqualTo(localDateTime);
        assertThat(offsetDateTime.getOffset()).isEqualTo(ZoneOffset.UTC);
    }

    @Test
    @DisplayName("should correctly map a list of API ItemPedido to domain ItemPedido list")
    void toItensDomain_shouldMapApiItemListToDomainItemList() {
        // given
        var apiItem = new br.com.b2b.infrastructure.openapi.pedido.model.ItemPedido();
        apiItem.setProduto("Test Product");
        apiItem.setQuantidade(3L);
        apiItem.setPrecoUnitario(new BigDecimal("99.99"));
        List<br.com.b2b.infrastructure.openapi.pedido.model.ItemPedido> apiItens = List.of(apiItem);

        // when
        List<ItemPedido> domainItens = pedidoMapper.toItensDomain(apiItens);

        // then
        assertThat(domainItens).hasSize(1);
        ItemPedido domainItem = domainItens.get(0);
        assertThat(domainItem.getProduto()).isEqualTo(apiItem.getProduto());
        assertThat(domainItem.getQuantidade()).isEqualTo(apiItem.getQuantidade());
        assertThat(domainItem.getPrecoUnitario().valor()).isEqualByComparingTo(apiItem.getPrecoUnitario());
    }

    @Test
    @DisplayName("should return an empty list when mapping an empty API ItemPedido list")
    void toItensDomain_whenListIsEmpty_shouldReturnEmptyList() {
        // when
        List<ItemPedido> domainItens = pedidoMapper.toItensDomain(Collections.emptyList());

        // then
        assertThat(domainItens).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("should correctly map a single API ItemPedido to domain ItemPedido")
    void toItemDomain_shouldMapApiItemToDomainItem() {
        // given
        var apiItem = new br.com.b2b.infrastructure.openapi.pedido.model.ItemPedido();
        apiItem.setProduto("Another Product");
        apiItem.setQuantidade(1L);
        apiItem.setPrecoUnitario(new BigDecimal("12.50"));

        // when
        ItemPedido domainItem = pedidoMapper.toItemDomain(apiItem);

        // then
        assertThat(domainItem).isNotNull();
        assertThat(domainItem.getProduto()).isEqualTo(apiItem.getProduto());
        assertThat(domainItem.getQuantidade()).isEqualTo(apiItem.getQuantidade());
        assertThat(domainItem.getPrecoUnitario().valor()).isEqualByComparingTo(apiItem.getPrecoUnitario());
    }

    @Test
    @DisplayName("should correctly map a list of domain ItemPedido to ItemResponse list")
    void toItensResponse_shouldMapDomainItemListToResponseList() {
        // given
        Pedido pedido = createTestPedido();
        List<ItemPedido> domainItens = pedido.getItens();

        // when
        List<ItemResponse> responseItens = pedidoMapper.toItensResponse(domainItens);

        // then
        assertThat(responseItens).hasSize(2);
        assertThat(responseItens.get(0).getProduto()).isEqualTo(domainItens.get(0).getProduto());
        assertThat(responseItens.get(1).getPrecoUnitario()).isEqualByComparingTo(domainItens.get(1).getPrecoUnitario().valor());
    }

    @Test
    @DisplayName("should correctly map a single domain ItemPedido to ItemResponse")
    void toItemResponse_shouldMapDomainItemToResponseItem() {
        // given
        ItemPedido domainItem = createTestPedido().getItens().get(0);

        // when
        ItemResponse responseItem = pedidoMapper.toItemResponse(domainItem);

        // then
        assertThat(responseItem).isNotNull();
        assertThat(responseItem.getId()).isEqualTo(domainItem.getId());
        assertThat(responseItem.getProduto()).isEqualTo(domainItem.getProduto());
        assertThat(responseItem.getQuantidade()).isEqualTo(domainItem.getQuantidade());
        assertThat(responseItem.getPrecoUnitario()).isEqualByComparingTo(domainItem.getPrecoUnitario().valor());
        assertThat(responseItem.getSubTotal()).isEqualByComparingTo(domainItem.getSubtotal().valor());
    }

    @Test
    @DisplayName("should correctly map a Pagination of Pedido to PedidoPaginadoResponse")
    void toResponsePagination_shouldMapPaginationObject() {
        // given
        List<Pedido> pedidos = List.of(createTestPedido());
        Pagination<Pedido> pedidoPagination = new Pagination<>(0, 10, 1L, pedidos);

        // when
        PedidoPaginadoResponse paginatedResponse = pedidoMapper.toResponsePagination(pedidoPagination);

        // then
        assertThat(paginatedResponse).isNotNull();
        assertThat(paginatedResponse.getCurrentPage()).isEqualTo(pedidoPagination.currentPage());
        assertThat(paginatedResponse.getPerPage()).isEqualTo(pedidoPagination.perPage());
        assertThat(paginatedResponse.getTotal()).isEqualTo(pedidoPagination.total());
        assertThat(paginatedResponse.getItems()).hasSize(1);
        assertThat(paginatedResponse.getItems().get(0).getId()).isEqualTo(pedidos.get(0).getId());
    }

    // --- Helper Methods ---

    private Pedido createTestPedido() {
        UUID pedidoId = UUID.fromString("a9c3f3b6-3b3b-4b3b-8b3b-3b3b3b3b3b3b");
        Long idParceiro = 123L;
        LocalDateTime now = LocalDateTime.now();

        Pedido pedido = Pedido.builder()
                .id(pedidoId)
                .idParceiro(idParceiro)
                .status(StatusPedido.PENDENTE)
                .dataCriacao(now)
                .dataUltimaAtualizacao(now.plusHours(1))
                .build();

        ItemPedido item1 = ItemPedido.builder()
                .id(UUID.fromString("c1a1b1c1-a1b1-c1a1-b1c1-a1b1c1a1b1c1"))
                .produto("Produto A")
                .quantidade(2)
                .precoUnitario(new ValorMonetario(new BigDecimal("10.00")))
                .subtotal(new ValorMonetario(new BigDecimal("20.00")))
                .pedido(pedido)
                .build();

        ItemPedido item2 = ItemPedido.builder()
                .id(UUID.fromString("d2b2c2d2-b2c2-d2b2-c2d2-b2c2d2b2c2d2"))
                .produto("Produto B")
                .quantidade(1)
                .precoUnitario(new ValorMonetario(new BigDecimal("15.50")))
                .subtotal(new ValorMonetario(new BigDecimal("15.50")))
                .pedido(pedido)
                .build();

        List<ItemPedido> itens = List.of(item1, item2);

        // Re-build the Pedido with all its associations for a complete object
        return Pedido.builder()
                .id(pedidoId)
                .idParceiro(idParceiro)
                .itens(itens)
                .valorTotal(new ValorMonetario(new BigDecimal("35.50")))
                .status(StatusPedido.PENDENTE)
                .dataCriacao(now)
                .dataUltimaAtualizacao(now.plusHours(1))
                .build();
    }
}
