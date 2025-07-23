package br.com.b2b.application.port.out;

import br.com.b2b.domain.model.Pedido;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface NotificacaoPort {
    void notificarMudancaStatus(Pedido pedido) throws JsonProcessingException;
}
