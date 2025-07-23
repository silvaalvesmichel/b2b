package br.com.b2b.application.service;

import br.com.b2b.application.port.in.CreditoParceiroUseCase;
import br.com.b2b.application.port.out.ParceiroCreditoPort;
import br.com.b2b.domain.exception.DomainException;
import br.com.b2b.domain.model.CreditoParceiro;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CreditoParceiroServiceImpl implements CreditoParceiroUseCase {

  private final ParceiroCreditoPort parceiroCreditoPort;

    @Override
    public CreditoParceiro criarCreditoParceiro(Long idParceiro, BigDecimal credito) {
        CreditoParceiro creditoParceiroExist = parceiroCreditoPort.buscarPorIdParceiro(idParceiro);
        if(Objects.nonNull(creditoParceiroExist)) {
          throw new DomainException("Parceiro já cadastrado");
        }
        CreditoParceiro creditoParceiro = CreditoParceiro.newCreditoParceiro(idParceiro, credito);
        return parceiroCreditoPort.criarParceiroCredito(creditoParceiro);
    }
}
