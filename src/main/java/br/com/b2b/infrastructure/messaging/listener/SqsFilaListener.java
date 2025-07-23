package br.com.b2b.infrastructure.messaging.listener;

import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class SqsFilaListener {

    @SqsListener("minha-fila")
    public void processarMensagem(String mensagem) {
        System.out.println("📥 Recebido da fila: " + mensagem);
    }
}

