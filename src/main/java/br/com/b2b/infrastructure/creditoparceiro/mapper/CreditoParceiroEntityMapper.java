package br.com.b2b.infrastructure.creditoparceiro.mapper;

import br.com.b2b.domain.model.CreditoParceiro;
import br.com.b2b.infrastructure.creditoparceiro.entity.CreditoParceiroEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public abstract class CreditoParceiroEntityMapper {

    public static final CreditoParceiroEntityMapper INSTANCE = Mappers.getMapper(CreditoParceiroEntityMapper.class);

    public CreditoParceiroEntity toEntity(CreditoParceiro creditoParceiro) {
        CreditoParceiroEntity entity = new CreditoParceiroEntity();
        entity.setIdParceiro(creditoParceiro.getIdParceiro());
        entity.setId(creditoParceiro.getId());
        entity.setCredito(creditoParceiro.getCredito());
        return entity;
    }

    public CreditoParceiro toDomain(CreditoParceiroEntity entity) {
        return CreditoParceiro.builder()
                .id(entity.getId())
                .credito(entity.getCredito())
                .idParceiro(entity.getIdParceiro())
                .build();
    }
}
