package br.com.b2b.infrastructure.pedido.entity;

import br.com.b2b.domain.model.Pedido;
import br.com.b2b.domain.model.enums.StatusPedido;
import br.com.b2b.infrastructure.pedido.mapper.PedidoEntityMapper;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
public class PedidoEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private Long idParceiro;

    @OneToMany(
            mappedBy = "pedido", //indica que o lado ItemPedidoEntity gerencia a relacao
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER // para este caso, Eager simplifica o mapeamento
    )
    private List<ItemEntity> itens;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valorTotal;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @Column(nullable = false)
    private LocalDateTime dataUltimaAtualizacao;

    @Builder
    public PedidoEntity(UUID id, Long idParceiro, List<ItemEntity> itens, BigDecimal valorTotal, StatusPedido status, LocalDateTime dataCriacao, LocalDateTime dataUltimaAtualizacao) {
        this.id = id;
        this.idParceiro = idParceiro;
        this.itens = itens;
        this.valorTotal = valorTotal;
        this.status = status;
        this.dataCriacao = dataCriacao;
        this.dataUltimaAtualizacao = dataUltimaAtualizacao;
    }

    public static PedidoEntity from(final Pedido pedido){
        return PedidoEntityMapper.INSTANCE.toEntity(pedido);
    }

    public Pedido toDomain() {
        return PedidoEntityMapper.INSTANCE.toDomain(this);
    }

}
