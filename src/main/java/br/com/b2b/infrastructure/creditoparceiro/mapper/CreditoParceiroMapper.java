package br.com.b2b.infrastructure.creditoparceiro.mapper;

import br.com.b2b.domain.model.CreditoParceiro;
import br.com.b2b.infrastructure.openapi.credito.parceiro.model.ParceiroCreditoResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class CreditoParceiroMapper {

    public ParceiroCreditoResponse toResponse(CreditoParceiro creditoParceiro) {
        ParceiroCreditoResponse response = new ParceiroCreditoResponse();
        response.setId(creditoParceiro.getId());
        response.setIdParceiro(creditoParceiro.getIdParceiro());
        response.setCredito(creditoParceiro.getCredito());
        return response;
    }
}
