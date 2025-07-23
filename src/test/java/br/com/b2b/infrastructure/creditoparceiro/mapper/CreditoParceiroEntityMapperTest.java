package br.com.b2b.infrastructure.creditoparceiro.mapper;


import br.com.b2b.domain.model.CreditoParceiro;
import br.com.b2b.infrastructure.creditoparceiro.entity.CreditoParceiroEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CreditoParceiroEntityMapperTest {

    private final CreditoParceiroEntityMapper mapper = CreditoParceiroEntityMapper.INSTANCE;

    @Test
    @DisplayName("Should correctly map CreditoParceiro domain to CreditoParceiroEntity")
    void toEntity() {
        // Arrange
        CreditoParceiro creditoParceiro = CreditoParceiro.builder()
                .id(UUID.randomUUID())
                .idParceiro(1L)
                .credito(new BigDecimal("1500.75"))
                .build();

        // Act
        CreditoParceiroEntity entity = mapper.toEntity(creditoParceiro);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(creditoParceiro.getId());
        assertThat(entity.getIdParceiro()).isEqualTo(creditoParceiro.getIdParceiro());
        assertThat(entity.getCredito()).isEqualByComparingTo(creditoParceiro.getCredito());
    }

    @Test
    @DisplayName("Should correctly map CreditoParceiroEntity to CreditoParceiro domain")
    void toDomain() {
        // Arrange
        CreditoParceiroEntity entity = new CreditoParceiroEntity();
        entity.setId(UUID.randomUUID());
        entity.setIdParceiro(2L);
        entity.setCredito(new BigDecimal("2500.50"));

        // Act
        CreditoParceiro creditoParceiro = mapper.toDomain(entity);

        // Assert
        assertThat(creditoParceiro).isNotNull();
        assertThat(creditoParceiro.getId()).isEqualTo(entity.getId());
        assertThat(creditoParceiro.getIdParceiro()).isEqualTo(entity.getIdParceiro());
        assertThat(creditoParceiro.getCredito()).isEqualByComparingTo(entity.getCredito());
    }
}
