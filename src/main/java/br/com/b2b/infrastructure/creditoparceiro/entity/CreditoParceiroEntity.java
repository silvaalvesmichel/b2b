package br.com.b2b.infrastructure.creditoparceiro.entity;

import br.com.b2b.domain.model.CreditoParceiro;
import br.com.b2b.infrastructure.creditoparceiro.mapper.CreditoParceiroEntityMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "credito_parceiro")
@Getter
@Setter
@NoArgsConstructor
public class CreditoParceiroEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private Long idParceiro;

    @Column(nullable = false)
    private BigDecimal credito;

    public static CreditoParceiroEntity from(final CreditoParceiro creditoParceiro){
        return CreditoParceiroEntityMapper.INSTANCE.toEntity(creditoParceiro);
    }

    public CreditoParceiro toDomain() {
        return CreditoParceiroEntityMapper.INSTANCE.toDomain(this);
    }
}
