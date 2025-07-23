package br.com.b2b.infrastructure.creditoparceiro.api;

import br.com.b2b.application.port.in.CreditoParceiroUseCase;
import br.com.b2b.domain.model.CreditoParceiro;
import br.com.b2b.infrastructure.creditoparceiro.mapper.CreditoParceiroMapper;
import br.com.b2b.infrastructure.openapi.credito.parceiro.api.CreditoParceiroApiDelegate;
import br.com.b2b.infrastructure.openapi.credito.parceiro.model.ParceiroCreditoRequest;
import br.com.b2b.infrastructure.openapi.credito.parceiro.model.ParceiroCreditoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreditoParceiroApiDelegateImpl implements CreditoParceiroApiDelegate {

    private final CreditoParceiroUseCase useCase;
    private final CreditoParceiroMapper mapper;

    @Override
    public ResponseEntity<ParceiroCreditoResponse> criar(ParceiroCreditoRequest request) throws Exception {
        CreditoParceiro creditoParceiro=  useCase.criarCreditoParceiro(request.getIdParceiro(), request.getCredito());
        return ResponseEntity.ok().body(mapper.toResponse(creditoParceiro));
    }
}
