package br.com.b2b.infrastructure.pedido.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "itens")
@Getter
@Setter
@NoArgsConstructor
public class ItemEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY) // o item pertence a um pedido
    @JoinColumn(name = "pedido_id", nullable = false) // chave estrangeira
    private PedidoEntity pedido;

    @Column(nullable = false)
    private String produto;

    @Column(nullable = false)
    private long quantidade;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal precoUnitario;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal subTotal;

    @Builder
    public ItemEntity(UUID id, PedidoEntity pedido, String produto, long quantidade, BigDecimal precoUnitario, BigDecimal subTotal) {
        this.id = id;
        this.pedido = pedido;
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.subTotal = subTotal;
    }


}
