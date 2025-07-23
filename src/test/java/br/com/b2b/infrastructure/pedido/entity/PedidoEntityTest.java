package br.com.b2b.infrastructure.pedido.entity;


import br.com.b2b.domain.model.enums.StatusPedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes para PedidoEntity")
class PedidoEntityTest {

    private UUID id;
    private Long idParceiro;
    private List<ItemEntity> itensEntity;
    private BigDecimal valorTotal;
    private StatusPedido status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataUltimaAtualizacao;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        idParceiro = 1L;
        valorTotal = new BigDecimal("199.98");
        status = StatusPedido.PENDENTE;
        dataCriacao = LocalDateTime.now().minusDays(1);
        dataUltimaAtualizacao = LocalDateTime.now();

        // Mock ItemEntity for testing purposes
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(UUID.randomUUID());
        itemEntity.setProduto("Produto Teste");
        itemEntity.setQuantidade(2);
        itemEntity.setPrecoUnitario(new BigDecimal("99.99"));
        itensEntity = Collections.singletonList(itemEntity);
    }

    @Test
    @DisplayName("Deve criar PedidoEntity com construtor padrão e setar valores")
    void testNoArgsConstructorAndSetters() {
        // Arrange
        PedidoEntity pedidoEntity = new PedidoEntity();
        pedidoEntity.setId(id);
        pedidoEntity.setIdParceiro(idParceiro);
        pedidoEntity.setItens(itensEntity);
        pedidoEntity.setValorTotal(valorTotal);
        pedidoEntity.setStatus(status);
        pedidoEntity.setDataCriacao(dataCriacao);
        pedidoEntity.setDataUltimaAtualizacao(dataUltimaAtualizacao);

        // Assert
        assertThat(pedidoEntity.getId()).isEqualTo(id);
        assertThat(pedidoEntity.getIdParceiro()).isEqualTo(idParceiro);
        assertThat(pedidoEntity.getItens()).isEqualTo(itensEntity);
        assertThat(pedidoEntity.getValorTotal()).isEqualTo(valorTotal);
        assertThat(pedidoEntity.getStatus()).isEqualTo(status);
        assertThat(pedidoEntity.getDataCriacao()).isEqualTo(dataCriacao);
        assertThat(pedidoEntity.getDataUltimaAtualizacao()).isEqualTo(dataUltimaAtualizacao);
    }

    @Test
    @DisplayName("Deve criar PedidoEntity usando o Builder")
    void testBuilder() {
        // Arrange & Act
        PedidoEntity pedidoEntity = PedidoEntity.builder()
                .id(id)
                .idParceiro(idParceiro)
                .itens(itensEntity)
                .valorTotal(valorTotal)
                .status(status)
                .dataCriacao(dataCriacao)
                .dataUltimaAtualizacao(dataUltimaAtualizacao)
                .build();

        // Assert
        assertThat(pedidoEntity.getId()).isEqualTo(id);
        assertThat(pedidoEntity.getIdParceiro()).isEqualTo(idParceiro);
        assertThat(pedidoEntity.getItens()).isEqualTo(itensEntity);
        assertThat(pedidoEntity.getValorTotal()).isEqualTo(valorTotal);
        assertThat(pedidoEntity.getStatus()).isEqualTo(status);
        assertThat(pedidoEntity.getDataCriacao()).isEqualTo(dataCriacao);
        assertThat(pedidoEntity.getDataUltimaAtualizacao()).isEqualTo(dataUltimaAtualizacao);
    }

}
