package br.com.b2b.domain.model;

import br.com.b2b.domain.exception.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para a classe ItemPedido")
class ItemPedidoTest {

    // The ItemPedido class is used as a factory, so we need an instance to call the method.
    private ItemPedido itemPedidoFactory;

    @Mock
    private Pedido mockPedido;

    // For these tests to work, it's assumed that your ValorMonetario
    // value object has a proper .equals() implementation that compares the underlying BigDecimal value.
    // e.g., using value.compareTo(other.value) == 0

    @BeforeEach
    void setUp() {
        // Initialize mocks for each test
        MockitoAnnotations.openMocks(this);
        // Create a dummy instance to access the factory method.
        // See the "Code Quality and Suggestions" section for an improvement here.
        itemPedidoFactory = ItemPedido.builder().build();
    }

    @Test
    @DisplayName("Deve criar um novo ItemPedido com sucesso quando os dados são válidos")
    void newItemPedido_deveCriarComSucesso_quandoDadosValidos() {
        // Arrange
        String produto = "Notebook Gamer";
        long quantidade = 2L;
        var precoUnitario = new ValorMonetario(new BigDecimal("5000.00"));
        var subtotalEsperado = new ValorMonetario(new BigDecimal("10000.00"));

        // Act
        ItemPedido novoItem = itemPedidoFactory.newItemPedido(mockPedido, produto, quantidade, precoUnitario);

        // Assert
        assertNotNull(novoItem);
        assertNotNull(novoItem.getId());
        assertEquals(produto, novoItem.getProduto());
        assertEquals(quantidade, novoItem.getQuantidade());
        assertEquals(precoUnitario, novoItem.getPrecoUnitario());
        assertEquals(mockPedido, novoItem.getPedido());
        assertEquals(subtotalEsperado, novoItem.getSubtotal());
    }

    @Test
    @DisplayName("Deve lançar DomainException quando o nome do produto for nulo")
    void newItemPedido_deveLancarExcecao_quandoProdutoForNulo() {
        // Arrange
        long quantidade = 1L;
        var precoUnitario = new ValorMonetario(new BigDecimal("100.00"));

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class, () -> {
            itemPedidoFactory.newItemPedido(mockPedido, null, quantidade, precoUnitario);
        });

        assertEquals("O nome do produto não pode ser vazio.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    @DisplayName("Deve lançar DomainException quando o nome do produto estiver em branco")
    void newItemPedido_deveLancarExcecao_quandoProdutoEstiverEmBranco(String produtoEmBranco) {
        // Arrange
        long quantidade = 1L;
        var precoUnitario = new ValorMonetario(new BigDecimal("100.00"));

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class, () -> {
            itemPedidoFactory.newItemPedido(mockPedido, produtoEmBranco, quantidade, precoUnitario);
        });

        assertEquals("O nome do produto não pode ser vazio.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar DomainException quando a quantidade for zero")
    void newItemPedido_deveLancarExcecao_quandoQuantidadeForZero() {
        // Arrange
        String produto = "Mouse";
        long quantidade = 0L;
        var precoUnitario = new ValorMonetario(new BigDecimal("50.00"));

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class, () -> {
            itemPedidoFactory.newItemPedido(mockPedido, produto, quantidade, precoUnitario);
        });

        assertEquals("A quantidade do item deve ser positiva.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar DomainException quando a quantidade for negativa")
    void newItemPedido_deveLancarExcecao_quandoQuantidadeForNegativa() {
        // Arrange
        String produto = "Teclado";
        long quantidade = -5L;
        var precoUnitario = new ValorMonetario(new BigDecimal("150.00"));

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class, () -> {
            itemPedidoFactory.newItemPedido(mockPedido, produto, quantidade, precoUnitario);
        });

        assertEquals("A quantidade do item deve ser positiva.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve calcular o subtotal corretamente com valores decimais")
    void calcularSubtotal_deveRetornarValorCorreto() {
        // Arrange
        String produto = "Monitor 4K";
        long quantidade = 3L;
        var precoUnitario = new ValorMonetario(new BigDecimal("2500.50")); // 2500.50 * 3 = 7501.50
        var subtotalEsperado = new ValorMonetario(new BigDecimal("7501.50"));

        // Act
        ItemPedido novoItem = itemPedidoFactory.newItemPedido(mockPedido, produto, quantidade, precoUnitario);

        // Assert
        assertEquals(subtotalEsperado, novoItem.getSubtotal());
    }
}
