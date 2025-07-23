package br.com.b2b.domain.model;

import br.com.b2b.domain.exception.DomainException;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class ItemPedido {

    private UUID id;
    private String produto;
    private long quantidade;
    private ValorMonetario precoUnitario;
    private ValorMonetario subtotal;
    private Pedido pedido;

    @Builder
    public ItemPedido(UUID id, String produto, long quantidade, ValorMonetario precoUnitario, ValorMonetario subtotal, Pedido pedido) {
        this.id = id;
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.subtotal = subtotal;
        this.pedido = pedido;
    }

    public ItemPedido newItemPedido( Pedido pedido, String produto, long quantidade, ValorMonetario precoUnitario) {
        // Validações de guarda (Guard Clauses)
        if (produto == null || produto.isBlank()) {
            throw new DomainException("O nome do produto não pode ser vazio.");
        }
        if (quantidade <= 0) {
            throw new DomainException("A quantidade do item deve ser positiva.");
        }
        return ItemPedido.builder()
                .id(UUID.randomUUID())
                .produto(produto)
                .quantidade(quantidade)
                .precoUnitario(precoUnitario)
                .subtotal(calcularSubtotal(precoUnitario, quantidade))
                .pedido(pedido)
                .build();
    }

    private ValorMonetario calcularSubtotal(ValorMonetario precoUnitario, Long quantidade) {
        BigDecimal valorTotalItem = precoUnitario.valor().multiply(BigDecimal.valueOf(quantidade));
        return new ValorMonetario(valorTotalItem);
    }


}
