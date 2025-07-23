package br.com.b2b.application.port.in;

import br.com.b2b.domain.model.CreditoParceiro;

import java.math.BigDecimal;

public interface CreditoParceiroUseCase {

    CreditoParceiro criarCreditoParceiro(Long idParceiro, BigDecimal credito);
}
