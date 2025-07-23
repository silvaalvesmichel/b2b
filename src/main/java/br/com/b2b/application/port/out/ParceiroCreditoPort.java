package br.com.b2b.application.port.out;

import br.com.b2b.domain.model.CreditoParceiro;

import java.math.BigDecimal;

public interface ParceiroCreditoPort {

    boolean verificarCredito(Long idParceiro, BigDecimal valorPedido);

    void debitarCredito(Long idParceiro, BigDecimal valorPedido);

    CreditoParceiro criarParceiroCredito(CreditoParceiro creditoParceiro);

    CreditoParceiro buscarPorIdParceiro(Long idParceiro);
}
