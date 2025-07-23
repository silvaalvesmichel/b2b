package br.com.b2b.infrastructure.messaging;

import br.com.b2b.application.port.out.NotificacaoPort;
import br.com.b2b.domain.model.Pedido;
import br.com.b2b.infrastructure.messaging.publisher.SnsPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class LogNotificacaoAdapter implements NotificacaoPort {

    private final SnsPublisher snsPublisher;

    @Override
    public void notificarMudancaStatus(Pedido pedido) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String json = mapper.writeValueAsString(pedido);
        snsPublisher.publicar(json);
        log.info("[NOTIFICAÇÃO] Pedido {} do parceiro {} teve seu status alterado para {}", pedido.getId(), pedido.getIdParceiro(), pedido.getStatus());
    }
}
