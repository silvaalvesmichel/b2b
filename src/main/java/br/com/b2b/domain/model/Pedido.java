package br.com.b2b.domain.model;

import br.com.b2b.domain.exception.DomainException;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Pedido {

    private UUID id;
    private Long idParceiro;
    private List<ItemPedido> itens;
    private ValorMonetario valorTotal;
    private StatusPedido status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataUltimaAtualizacao;

    @Builder
    public Pedido(UUID id,
                  Long idParceiro,
                  List<ItemPedido> itens,
                  ValorMonetario valorTotal,
                  StatusPedido status,
                  LocalDateTime dataCriacao,
                  LocalDateTime dataUltimaAtualizacao) {
        this.id = id;
        this.idParceiro = idParceiro;
        this.itens = itens;
        this.valorTotal = valorTotal;
        this.status = status;
        this.dataCriacao = dataCriacao;
        this.dataUltimaAtualizacao = dataUltimaAtualizacao;
    }

    /*public static Pedido newPedido(Long idParceiro, List<ItemPedido> itens) {
        Objects.requireNonNull(idParceiro, "ID do parceiro é obrigatório.");
        Objects.requireNonNull(itens, "A lista de itens é obrigátoria");
        if (itens.isEmpty()) {
            throw new DomainException("Um pedido deve ter pelo menos um item.");
        }

        List<ItemPedido> newItens = itens.stream().map(item-> item.newItemPedido(item.getProduto(), item.getQuantidade(), item.getPrecoUnitario())).toList();

        return Pedido.builder()
                .id(UUID.randomUUID())
                .idParceiro(idParceiro)
                .itens(newItens)
                .status(StatusPedido.PENDENTE)
                .dataCriacao(LocalDateTime.now())
                .dataUltimaAtualizacao(LocalDateTime.now())
                .valorTotal(calcularValorTotal(newItens))
                .build();
    }*/

    public static Pedido newPedido(Long idParceiro, List<ItemPedido> itens) {
        Objects.requireNonNull(idParceiro, "ID do parceiro é obrigatório.");
        Objects.requireNonNull(itens, "A lista de itens é obrigatória");
        if (itens.isEmpty()) {
            throw new DomainException("Um pedido deve ter pelo menos um item.");
        }

        UUID pedidoId = UUID.randomUUID(); // gera ID antes para associar aos itens
        LocalDateTime agora = LocalDateTime.now();

        Pedido pedido = Pedido.builder()
                .id(pedidoId)
                .idParceiro(idParceiro)
                .status(StatusPedido.PENDENTE)
                .dataCriacao(agora)
                .dataUltimaAtualizacao(agora)
                .build();

        // Associa o pedido aos itens
        List<ItemPedido> newItens = itens.stream()
                .map(item -> item.newItemPedido(pedido, item.getProduto(), item.getQuantidade(), item.getPrecoUnitario()))
                .toList();

        // Setar os itens e valor total no pedido
        pedido.itens = newItens;
        pedido.valorTotal = calcularValorTotal(newItens);

        return pedido;
    }

    public static Pedido atualizarStatus(Pedido pedido, StatusPedido status) {
        return Pedido.builder()
                .id(pedido.getId())
                .idParceiro(pedido.getIdParceiro())
                .status(status)
                .dataCriacao(pedido.dataCriacao)
                .dataUltimaAtualizacao(LocalDateTime.now())
                .itens(pedido.getItens())
                .valorTotal(pedido.getValorTotal())
                .build();
    }


    public BigDecimal getValorTotalComoBigDecimal() {
        return this.valorTotal != null ? this.valorTotal.valor() : null;
    }

    private static ValorMonetario calcularValorTotal(List<ItemPedido> itens) {
        return itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(new ValorMonetario(BigDecimal.ZERO), ValorMonetario::adicionar);
    }

    public void aprovar() {
        if (this.status != StatusPedido.PENDENTE) {
            throw new DomainException("Apenas pedidos com status PENDENTE podem ser aprovados.");
        }
        this.status = StatusPedido.APROVADO;
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    public void cancelar() {
        if (this.status == StatusPedido.ENVIADO || this.status == StatusPedido.ENTREGUE) {
            throw new DomainException("Não é possível cancelar um pedido que já foi enviado ou entregue.");
        }
        if (this.status == StatusPedido.CANCELADO) {
            return; // Idempotência
        }
        this.status = StatusPedido.CANCELADO;
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }
}
