package br.com.b2b.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public record ValorMonetario(BigDecimal valor) {

    public ValorMonetario{
        Objects.requireNonNull(valor,"Valor não pode ser nulo");
        if(valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor monetário não pode ser negativo.");
        }
    }

    public ValorMonetario adicionar(ValorMonetario outro){
        return new ValorMonetario(this.valor.add(outro.valor));
    }

}
