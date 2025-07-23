package br.com.b2b.infrastructure.pedido.entity;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ItemEntityTest {

    @Test
    @DisplayName("Should create an instance with the no-args constructor and fields should be null or default")
    void testNoArgsConstructor() {
        // when
        ItemEntity item = new ItemEntity();

        // then
        assertNotNull(item);
        assertNull(item.getId());
        assertNull(item.getPedido());
        assertNull(item.getProduto());
        assertEquals(0L, item.getQuantidade());
        assertNull(item.getPrecoUnitario());
        assertNull(item.getSubTotal());
    }

    @Test
    @DisplayName("Should correctly set and get all fields")
    void testGettersAndSetters() {
        // given
        ItemEntity item = new ItemEntity();
        UUID id = UUID.randomUUID();
        PedidoEntity pedido = new PedidoEntity();
        String produto = "Test Product";
        long quantidade = 10L;
        BigDecimal precoUnitario = new BigDecimal("19.99");
        BigDecimal subTotal = new BigDecimal("199.90");

        // when
        item.setId(id);
        item.setPedido(pedido);
        item.setProduto(produto);
        item.setQuantidade(quantidade);
        item.setPrecoUnitario(precoUnitario);
        item.setSubTotal(subTotal);

        // then
        assertEquals(id, item.getId());
        assertEquals(pedido, item.getPedido());
        assertEquals(produto, item.getProduto());
        assertEquals(quantidade, item.getQuantidade());
        assertEquals(0, precoUnitario.compareTo(item.getPrecoUnitario()));
        assertEquals(0, subTotal.compareTo(item.getSubTotal()));
    }

    @Test
    @DisplayName("Should build a complete ItemEntity using the builder")
    void testBuilder() {
        // given
        UUID id = UUID.randomUUID();
        PedidoEntity pedido = new PedidoEntity();
        String produto = "Macbook Pro M3";
        long quantidade = 2L;
        BigDecimal precoUnitario = new BigDecimal("15000.00");
        BigDecimal subTotal = new BigDecimal("30000.00");

        // when
        ItemEntity item = ItemEntity.builder()
                .id(id)
                .pedido(pedido)
                .produto(produto)
                .quantidade(quantidade)
                .precoUnitario(precoUnitario)
                .subTotal(subTotal)
                .build();

        // then
        assertNotNull(item);
        assertEquals(id, item.getId());
        assertEquals(pedido, item.getPedido());
        assertEquals(produto, item.getProduto());
        assertEquals(quantidade, item.getQuantidade());
        assertEquals(0, precoUnitario.compareTo(item.getPrecoUnitario()));
        assertEquals(0, subTotal.compareTo(item.getSubTotal()));
    }
}
