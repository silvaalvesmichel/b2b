package br.com.b2b.infrastructure.messaging.publisher;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Service
public class SnsPublisher {

    private final SnsClient snsClient;
    private final String topicArn = "arn:aws:sns:us-east-1:000000000000:meu-topico";

    public SnsPublisher(SnsClient snsClient) {
        this.snsClient = snsClient;
    }

    public void publicar(String mensagem) {
        PublishRequest request = PublishRequest.builder()
                .message(mensagem)
                .topicArn(topicArn)
                .build();
        snsClient.publish(request);
    }
}

