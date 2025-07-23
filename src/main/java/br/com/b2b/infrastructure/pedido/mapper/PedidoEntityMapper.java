package br.com.b2b.infrastructure.pedido.mapper;

import br.com.b2b.domain.model.ItemPedido;
import br.com.b2b.domain.model.Pedido;
import br.com.b2b.domain.model.ValorMonetario;
import br.com.b2b.infrastructure.pedido.entity.ItemEntity;
import br.com.b2b.infrastructure.pedido.entity.PedidoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class PedidoEntityMapper {

    public static final PedidoEntityMapper INSTANCE = Mappers.getMapper(PedidoEntityMapper.class);

    @Mapping(target = "valorTotal", expression = "java(pedido.getValorTotalComoBigDecimal())")
    @Mapping(target = "id", expression = "java(pedido.getId())")
    @Mapping(target = "idParceiro", expression = "java(pedido.getIdParceiro())")
    @Mapping(target = "status", expression = "java(pedido.getStatus())")
    @Mapping(target = "dataCriacao", expression = "java(pedido.getDataCriacao())")
    @Mapping(target = "dataUltimaAtualizacao", expression = "java(pedido.getDataUltimaAtualizacao())")
    @Mapping(target = "itens", expression = "java(toItensEntity(pedido.getItens(), pedido))")
    public abstract PedidoEntity toEntity(Pedido pedido);

    @Mapping(target = "valorTotal", expression = "java(bigDecimalToValorMonetario(entity.getValorTotal()))")
    @Mapping(target = "id", expression = "java(entity.getId())")
    @Mapping(target = "idParceiro", expression = "java(entity.getIdParceiro())")
    @Mapping(target = "status", expression = "java(entity.getStatus())")
    @Mapping(target = "dataCriacao", expression = "java(entity.getDataCriacao())")
    @Mapping(target = "dataUltimaAtualizacao", expression = "java(entity.getDataUltimaAtualizacao())")
    @Mapping(target = "itens", expression = "java(toItensDomain(entity.getItens()))")
    public abstract Pedido toDomain(PedidoEntity entity);

    @Named("bigDecimalToValorMonetario")
    protected ValorMonetario bigDecimalToValorMonetario(BigDecimal valor) {
        return valor != null ? new ValorMonetario(valor) : null;
    }

    @Named("toItensEntity")
    protected List<ItemEntity> toItensEntity(List<ItemPedido> itens, Pedido pedido) {
        return itens.stream().map(itemPedido ->  this.toItemEntity(itemPedido, pedido)).collect(Collectors.toList());
    }

    private ItemEntity toItemEntity(ItemPedido itemPedido, Pedido pedido) {
        return ItemEntity.builder()
                .id(itemPedido.getId())
                .produto(itemPedido.getProduto())
                .precoUnitario(itemPedido.getPrecoUnitario().valor())
                .quantidade(itemPedido.getQuantidade())
                .subTotal(itemPedido.getSubtotal().valor())
                .pedido(toPedidoItemEntity(pedido))
                .build();
    }

    private PedidoEntity toPedidoItemEntity(Pedido pedido) {
         PedidoEntity entity = new PedidoEntity();
         entity.setId(pedido.getId());
         return entity;
    }

    @Named("toItensDomain")
    protected List<ItemPedido> toItensDomain(List<ItemEntity> itens) {
        return itens.stream().map(this::toItemDomain).collect(Collectors.toList());
    }

    private ItemPedido toItemDomain(ItemEntity itemPedido) {
        return ItemPedido.builder()
                .id(itemPedido.getId())
                .produto(itemPedido.getProduto())
                .precoUnitario(new ValorMonetario(itemPedido.getPrecoUnitario()))
                .quantidade(itemPedido.getQuantidade())
                .subtotal(new ValorMonetario(itemPedido.getSubTotal()))
                .build();
    }
}
