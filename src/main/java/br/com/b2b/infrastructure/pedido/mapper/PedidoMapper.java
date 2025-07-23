package br.com.b2b.infrastructure.pedido.mapper;

import br.com.b2b.domain.model.ItemPedido;
import br.com.b2b.domain.model.Pedido;
import br.com.b2b.domain.model.StatusPedido;
import br.com.b2b.domain.model.ValorMonetario;
import br.com.b2b.infrastructure.commons.Pagination;
import br.com.b2b.infrastructure.openapi.pedido.model.ItemResponse;
import br.com.b2b.infrastructure.openapi.pedido.model.PedidoPaginadoResponse;
import br.com.b2b.infrastructure.openapi.pedido.model.PedidoResponse;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class PedidoMapper {

    public PedidoResponse toResponse(Pedido pedido) {
        PedidoResponse response = new PedidoResponse();
        response.setId(pedido.getId());
        response.setValorTotal(pedido.getValorTotalComoBigDecimal());
        response.setIdParceiro(pedido.getIdParceiro());
        response.setStatus(mapStatus(pedido.getStatus()));
        response.setDataCriacao(mapDateTime(pedido.getDataCriacao()));
        response.setDataUltimaAtualizacao(mapDateTime(pedido.getDataUltimaAtualizacao()));
        response.setItens(toItensResponse(pedido.getItens()));
        return response;
    }

    public PedidoResponse.StatusEnum mapStatus(StatusPedido status) {
        return PedidoResponse.StatusEnum.valueOf(status.name());
    }

    public OffsetDateTime mapDateTime(LocalDateTime localDateTime) {
        return localDateTime.atOffset(ZoneOffset.UTC);
    }

    public List<ItemPedido> toItensDomain(List<br.com.b2b.infrastructure.openapi.pedido.model.ItemPedido> itens) {
        return itens.stream().map(this::toItemDomain).collect(Collectors.toList());
    }

    public ItemPedido toItemDomain(br.com.b2b.infrastructure.openapi.pedido.model.ItemPedido item) {
        return ItemPedido.builder()
                .produto(item.getProduto())
                .precoUnitario(new ValorMonetario(item.getPrecoUnitario()))
                .quantidade(item.getQuantidade())
                .build();
    }

    public List<ItemResponse> toItensResponse(List<ItemPedido> itens) {
        return itens.stream().map(this::toItemResponse).collect(Collectors.toList());
    }

    public ItemResponse toItemResponse(ItemPedido item) {
        ItemResponse response = new ItemResponse();
        response.setId(item.getId());
        response.setProduto(item.getProduto());
        response.setQuantidade(item.getQuantidade());
        response.setPrecoUnitario(item.getPrecoUnitario().valor());
        response.setSubTotal(item.getSubtotal().valor());
        return response;
    }


    public abstract PedidoPaginadoResponse toResponsePagination(Pagination<Pedido> pedidos);
}

