package br.com.b2b.infrastructure.creditoparceiro.entity;


import br.com.b2b.domain.model.CreditoParceiro;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class CreditoParceiroEntityTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        CreditoParceiroEntity entity = new CreditoParceiroEntity();
        UUID id = UUID.randomUUID();
        Long idParceiro = 123L;
        BigDecimal credito = new BigDecimal("1500.75");

        // Act
        entity.setId(id);
        entity.setIdParceiro(idParceiro);
        entity.setCredito(credito);

        // Assert
        assertEquals(id, entity.getId());
        assertEquals(idParceiro, entity.getIdParceiro());
        assertEquals(credito, entity.getCredito());
    }

    @Test
    void testNoArgsConstructor() {
        // Arrange & Act
        CreditoParceiroEntity entity = new CreditoParceiroEntity();

        // Assert
        assertNotNull(entity);
        assertNull(entity.getId(), "ID should be null for a new instance");
        assertNull(entity.getIdParceiro(), "idParceiro should be null for a new instance");
        assertNull(entity.getCredito(), "credito should be null for a new instance");
    }

    @Test
    void shouldConvertFromDomainObjectToEntity() {
        // Arrange
        CreditoParceiro domainObject = CreditoParceiro.builder()
                .id(UUID.randomUUID())
                .idParceiro(456L)
                .credito(new BigDecimal("2500.00"))
                .build();

        // Act
        CreditoParceiroEntity entity = CreditoParceiroEntity.from(domainObject);

        // Assert
        assertNotNull(entity);
        assertEquals(domainObject.getId(), entity.getId());
        assertEquals(domainObject.getIdParceiro(), entity.getIdParceiro());
        assertEquals(domainObject.getCredito(), entity.getCredito());
    }

    @Test
    void shouldConvertToDomainObjectFromEntity() {
        // Arrange
        CreditoParceiroEntity entity = new CreditoParceiroEntity();
        UUID id = UUID.randomUUID();
        Long idParceiro = 789L;
        BigDecimal credito = new BigDecimal("500.50");
        entity.setId(id);
        entity.setIdParceiro(idParceiro);
        entity.setCredito(credito);

        // Act
        CreditoParceiro domainObject = entity.toDomain();

        // Assert
        assertNotNull(domainObject);
        assertEquals(entity.getId(), domainObject.getId());
        assertEquals(entity.getIdParceiro(), domainObject.getIdParceiro());
        assertEquals(entity.getCredito(), domainObject.getCredito());
    }
}
