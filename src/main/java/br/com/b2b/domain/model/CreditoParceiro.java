package br.com.b2b.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class CreditoParceiro {

    private UUID id;
    private Long idParceiro;
    private BigDecimal credito;

    @Builder
    public CreditoParceiro(UUID id, Long idParceiro, BigDecimal credito) {
        this.id = id;
        this.idParceiro = idParceiro;
        this.credito = credito;
    }

    public static CreditoParceiro newCreditoParceiro(Long idParceiro, BigDecimal credito){
        return CreditoParceiro.builder()
                .id(UUID.randomUUID())
                .idParceiro(idParceiro)
                .credito(credito)
                .build();
    }

    public static CreditoParceiro atualizarCredito(CreditoParceiro creditoParceiro, BigDecimal credito){
        return CreditoParceiro.builder()
                .id(creditoParceiro.getId())
                .idParceiro(creditoParceiro.getIdParceiro())
                .credito(credito)
                .build();
    }
}
