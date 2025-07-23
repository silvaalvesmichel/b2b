package br.com.b2b.infrastructure.creditoparceiro.repository;


import br.com.b2b.domain.model.CreditoParceiro;
import br.com.b2b.infrastructure.commons.exeception.InfrastructureException;
import br.com.b2b.infrastructure.creditoparceiro.entity.CreditoParceiroEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ParceiroCreditoAdapter Tests")
class ParceiroCreditoRepositoryAdapterTest {

    @Mock
    private CreditoParceiroRepository repository;

    @InjectMocks
    private ParceiroCreditoRepositoryAdapter adapter;

    private Long idParceiro;
    private UUID id;
    private CreditoParceiroEntity creditoParceiroEntity;

    @BeforeEach
    void setUp() {
        idParceiro = 123L;
        id = UUID.randomUUID();
        creditoParceiroEntity = new CreditoParceiroEntity(id, idParceiro, new BigDecimal("1000.00"));
    }

    @Nested
    @DisplayName("verificarCredito method")
    class VerificarCreditoTests {

        @Test
        @DisplayName("Should return true when partner has sufficient credit")
        void verificarCredito_shouldReturnTrue_whenCreditIsSufficient() {
            // Arrange
            when(repository.findByIdParceiro(idParceiro)).thenReturn(Optional.of(creditoParceiroEntity));
            BigDecimal valorPedido = new BigDecimal("500.00");

            // Act
            boolean result = adapter.verificarCredito(idParceiro, valorPedido);

            // Assert
            assertThat(result).isTrue();
            verify(repository).findByIdParceiro(idParceiro);
        }

        @Test
        @DisplayName("Should return true when partner has exact credit for the order")
        void verificarCredito_shouldReturnTrue_whenCreditIsExact() {
            // Arrange
            when(repository.findByIdParceiro(idParceiro)).thenReturn(Optional.of(creditoParceiroEntity));
            BigDecimal valorPedido = new BigDecimal("1000.00");

            // Act
            boolean result = adapter.verificarCredito(idParceiro, valorPedido);

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should return false when partner has insufficient credit")
        void verificarCredito_shouldReturnFalse_whenCreditIsInsufficient() {
            // Arrange
            when(repository.findByIdParceiro(idParceiro)).thenReturn(Optional.of(creditoParceiroEntity));
            BigDecimal valorPedido = new BigDecimal("1500.00");

            // Act
            boolean result = adapter.verificarCredito(idParceiro, valorPedido);

            // Assert
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should throw InfrastructureException when partner is not found")
        void verificarCredito_shouldThrowException_whenPartnerNotFound() {
            // Arrange
            when(repository.findByIdParceiro(idParceiro)).thenReturn(Optional.empty());
            BigDecimal valorPedido = new BigDecimal("100.00");

            // Act & Assert
            assertThatThrownBy(() -> adapter.verificarCredito(idParceiro, valorPedido))
                    .isInstanceOf(InfrastructureException.class)
                    .hasMessage("Parceiro não cadastrado, realize o cadastro do Parceiro");
        }
    }

    @Nested
    @DisplayName("debitarCredito method")
    class DebitarCreditoTests {

        @Test
        @DisplayName("Should debit credit and save the updated entity")
        void debitarCredito_shouldDebitAndSave() {
            // Arrange
            when(repository.findByIdParceiro(idParceiro)).thenReturn(Optional.of(creditoParceiroEntity));
            BigDecimal valorPedido = new BigDecimal("300.00");
            ArgumentCaptor<CreditoParceiroEntity> captor = ArgumentCaptor.forClass(CreditoParceiroEntity.class);

            // Act
            adapter.debitarCredito(idParceiro, valorPedido);

            // Assert
            verify(repository).save(captor.capture());
            CreditoParceiroEntity savedEntity = captor.getValue();

            assertThat(savedEntity.getId()).isEqualTo(id);
            assertThat(savedEntity.getIdParceiro()).isEqualTo(idParceiro);
            assertThat(savedEntity.getCredito()).isEqualByComparingTo("700.00");
        }

        @Test
        @DisplayName("Should throw NoSuchElementException when partner is not found")
        void debitarCredito_shouldThrowException_whenPartnerNotFound() {
            // Arrange
            when(repository.findByIdParceiro(idParceiro)).thenReturn(Optional.empty());
            BigDecimal valorPedido = new BigDecimal("100.00");

            // Act & Assert
            // Note: The current implementation throws NoSuchElementException. See recommendations for improvement.
            assertThatThrownBy(() -> adapter.debitarCredito(idParceiro, valorPedido))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Test
    @DisplayName("criarParceiroCredito should save and return domain object")
    void criarParceiroCredito_shouldCreateAndReturnDomainObject() {
        // Arrange
        CreditoParceiro newCreditoParceiro = CreditoParceiro.newCreditoParceiro(idParceiro, new BigDecimal("5000.00"));
        CreditoParceiroEntity entityToSaveAndReturn = CreditoParceiroEntity.from(newCreditoParceiro);

        when(repository.save(any(CreditoParceiroEntity.class))).thenReturn(entityToSaveAndReturn);

        // Act
        CreditoParceiro result = adapter.criarParceiroCredito(newCreditoParceiro);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(newCreditoParceiro.getId());
        assertThat(result.getIdParceiro()).isEqualTo(idParceiro);
        assertThat(result.getCredito()).isEqualByComparingTo("5000.00");

        verify(repository).save(any(CreditoParceiroEntity.class));
    }

    @Nested
    @DisplayName("buscarPorIdParceiro method")
    class BuscarPorIdParceiroTests {

        @Test
        @DisplayName("Should return domain object when partner is found")
        void buscarPorIdParceiro_shouldReturnDomainObject_whenPartnerFound() {
            // Arrange
            when(repository.findByIdParceiro(idParceiro)).thenReturn(Optional.of(creditoParceiroEntity));

            // Act
            CreditoParceiro result = adapter.buscarPorIdParceiro(idParceiro);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(id);
            assertThat(result.getIdParceiro()).isEqualTo(idParceiro);
            assertThat(result.getCredito()).isEqualByComparingTo(creditoParceiroEntity.getCredito());
        }

        @Test
        @DisplayName("Should return null when partner is not found")
        void buscarPorIdParceiro_shouldReturnNull_whenPartnerNotFound() {
            // Arrange
            when(repository.findByIdParceiro(idParceiro)).thenReturn(Optional.empty());

            // Act
            CreditoParceiro result = adapter.buscarPorIdParceiro(idParceiro);

            // Assert
            assertThat(result).isNull();
        }
    }
}
