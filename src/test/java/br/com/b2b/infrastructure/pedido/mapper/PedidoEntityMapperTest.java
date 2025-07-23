package br.com.b2b.infrastructure.pedido.mapper;


import br.com.b2b.domain.model.ItemPedido;
import br.com.b2b.domain.model.Pedido;
import br.com.b2b.domain.model.ValorMonetario;
import br.com.b2b.domain.model.enums.StatusPedido;
import br.com.b2b.infrastructure.pedido.entity.ItemEntity;
import br.com.b2b.infrastructure.pedido.entity.PedidoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PedidoEntityMapper Test")
class PedidoEntityMapperTest {

    private PedidoEntityMapper mapper;

    @BeforeEach
    void setUp() {
        // Initialize the mapper instance before each test
        mapper = Mappers.getMapper(PedidoEntityMapper.class);
    }

    @Test
    @DisplayName("Should correctly map Pedido domain to PedidoEntity")
    void toEntity_whenGivenValidPedido_shouldMapToPedidoEntity() {
        // Arrange
        Pedido pedido = createTestPedidoDomain();

        // Act
        PedidoEntity pedidoEntity = mapper.toEntity(pedido);

        // Assert
        assertThat(pedidoEntity).isNotNull();
        assertThat(pedidoEntity.getId()).isEqualTo(pedido.getId());
        assertThat(pedidoEntity.getIdParceiro()).isEqualTo(pedido.getIdParceiro());
        assertThat(pedidoEntity.getStatus()).isEqualTo(pedido.getStatus());
        assertThat(pedidoEntity.getDataCriacao()).isEqualTo(pedido.getDataCriacao());
        assertThat(pedidoEntity.getDataUltimaAtualizacao()).isEqualTo(pedido.getDataUltimaAtualizacao());
        assertThat(pedidoEntity.getValorTotal()).isEqualByComparingTo(pedido.getValorTotalComoBigDecimal());

        assertThat(pedidoEntity.getItens()).isNotNull().hasSize(1);

        ItemPedido itemPedido = pedido.getItens().get(0);
        ItemEntity itemEntity = pedidoEntity.getItens().get(0);

        assertThat(itemEntity.getId()).isEqualTo(itemPedido.getId());
        assertThat(itemEntity.getProduto()).isEqualTo(itemPedido.getProduto());
        assertThat(itemEntity.getQuantidade()).isEqualTo(itemPedido.getQuantidade());
        assertThat(itemEntity.getPrecoUnitario()).isEqualByComparingTo(itemPedido.getPrecoUnitario().valor());
        assertThat(itemEntity.getSubTotal()).isEqualByComparingTo(itemPedido.getSubtotal().valor());

        // Verify the back-reference from item to pedido is correctly set
        assertThat(itemEntity.getPedido()).isNotNull();
        assertThat(itemEntity.getPedido().getId()).isEqualTo(pedido.getId());
    }

    @Test
    @DisplayName("Should correctly map PedidoEntity to Pedido domain")
    void toDomain_whenGivenValidPedidoEntity_shouldMapToPedidoDomain() {
        // Arrange
        PedidoEntity pedidoEntity = createTestPedidoEntity();

        // Act
        Pedido pedido = mapper.toDomain(pedidoEntity);

        // Assert
        assertThat(pedido).isNotNull();
        assertThat(pedido.getId()).isEqualTo(pedidoEntity.getId());
        assertThat(pedido.getIdParceiro()).isEqualTo(pedidoEntity.getIdParceiro());
        assertThat(pedido.getStatus()).isEqualTo(pedidoEntity.getStatus());
        assertThat(pedido.getDataCriacao()).isEqualTo(pedidoEntity.getDataCriacao());
        assertThat(pedido.getDataUltimaAtualizacao()).isEqualTo(pedidoEntity.getDataUltimaAtualizacao());
        assertThat(pedido.getValorTotal().valor()).isEqualByComparingTo(pedidoEntity.getValorTotal());

        assertThat(pedido.getItens()).isNotNull().hasSize(1);

        ItemEntity itemEntity = pedidoEntity.getItens().get(0);
        ItemPedido itemPedido = pedido.getItens().get(0);

        assertThat(itemPedido.getId()).isEqualTo(itemEntity.getId());
        assertThat(itemPedido.getProduto()).isEqualTo(itemEntity.getProduto());
        assertThat(itemPedido.getQuantidade()).isEqualTo(itemEntity.getQuantidade());
        assertThat(itemPedido.getPrecoUnitario().valor()).isEqualByComparingTo(itemEntity.getPrecoUnitario());
        assertThat(itemPedido.getSubtotal().valor()).isEqualByComparingTo(itemEntity.getSubTotal());
    }

    @Test
    @DisplayName("Should handle null valor when mapping BigDecimal to ValorMonetario")
    void toDomain_whenValorTotalIsNull_shouldMapToNull() {
        // Arrange
        PedidoEntity entityWithNullValor = createTestPedidoEntity();
        entityWithNullValor.setValorTotal(null);

        // Act
        Pedido pedido = mapper.toDomain(entityWithNullValor);

        // Assert
        assertThat(pedido.getValorTotal()).isNull();
    }

    @Test
    @DisplayName("Should handle null valor when mapping ValorMonetario to BigDecimal")
    void toEntity_whenValorTotalIsNull_shouldMapToNull() {
        // Arrange
        Pedido pedidoWithNullValor = Pedido.builder()
                .id(UUID.randomUUID())
                .valorTotal(null) // Set valorTotal to null
                .itens(Collections.emptyList())
                .build();

        // Act
        PedidoEntity pedidoEntity = mapper.toEntity(pedidoWithNullValor);

        // Assert
        assertThat(pedidoEntity.getValorTotal()).isNull();
    }

    @Test
    @DisplayName("Should handle empty item list when mapping from domain to entity")
    void toEntity_withEmptyItemList_shouldMapToEmptyList() {
        // Arrange
        Pedido pedido = Pedido.builder()
                .id(UUID.randomUUID())
                .idParceiro(1L)
                .status(StatusPedido.PENDENTE)
                .dataCriacao(LocalDateTime.now())
                .dataUltimaAtualizacao(LocalDateTime.now())
                .valorTotal(new ValorMonetario(BigDecimal.ZERO))
                .itens(Collections.emptyList()) // Empty list
                .build();

        // Act
        PedidoEntity pedidoEntity = mapper.toEntity(pedido);

        // Assert
        assertThat(pedidoEntity).isNotNull();
        assertThat(pedidoEntity.getItens()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Should handle empty item list when mapping from entity to domain")
    void toDomain_withEmptyItemList_shouldMapToEmptyList() {
        // Arrange
        PedidoEntity pedidoEntity = PedidoEntity.builder()
                .id(UUID.randomUUID())
                .idParceiro(1L)
                .status(StatusPedido.PENDENTE)
                .dataCriacao(LocalDateTime.now())
                .dataUltimaAtualizacao(LocalDateTime.now())
                .valorTotal(BigDecimal.ZERO)
                .itens(Collections.emptyList()) // Empty list
                .build();

        // Act
        Pedido pedido = mapper.toDomain(pedidoEntity);

        // Assert
        assertThat(pedido).isNotNull();
        assertThat(pedido.getItens()).isNotNull().isEmpty();
    }

    // region Helper Methods
    private Pedido createTestPedidoDomain() {
        UUID pedidoId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        // Create a base Pedido object without items first
        Pedido pedido = Pedido.builder()
                .id(pedidoId)
                .idParceiro(123L)
                .status(StatusPedido.APROVADO)
                .dataCriacao(now)
                .dataUltimaAtualizacao(now)
                .valorTotal(new ValorMonetario(new BigDecimal("200.50")))
                .build();

        // Create the item and associate it with the pedido
        ItemPedido itemPedido = ItemPedido.builder()
                .id(UUID.randomUUID())
                .produto("Test Product")
                .quantidade(2L)
                .precoUnitario(new ValorMonetario(new BigDecimal("100.25")))
                .subtotal(new ValorMonetario(new BigDecimal("200.50")))
                .pedido(pedido)
                .build();

        // Return a new Pedido instance with the complete list of items.
        // This correctly simulates the state of the domain object.
        return new Pedido(
                pedido.getId(),
                pedido.getIdParceiro(),
                List.of(itemPedido),
                pedido.getValorTotal(),
                pedido.getStatus(),
                pedido.getDataCriacao(),
                pedido.getDataUltimaAtualizacao()
        );
    }

    private PedidoEntity createTestPedidoEntity() {
        // Create the parent PedidoEntity
        PedidoEntity pedidoEntity = PedidoEntity.builder()
                .id(UUID.randomUUID())
                .idParceiro(456L)
                .status(StatusPedido.PENDENTE)
                .dataCriacao(LocalDateTime.now())
                .dataUltimaAtualizacao(LocalDateTime.now())
                .valorTotal(new BigDecimal("500.00"))
                .build();

        // Create the child ItemEntity and associate it with the parent
        ItemEntity itemEntity = ItemEntity.builder()
                .id(UUID.randomUUID())
                .produto("Test Entity Product")
                .quantidade(5L)
                .precoUnitario(new BigDecimal("100.00"))
                .subTotal(new BigDecimal("500.00"))
                .pedido(pedidoEntity)
                .build();

        // Set the list of items on the parent entity
        pedidoEntity.setItens(List.of(itemEntity));
        return pedidoEntity;
    }
    // endregion
}
