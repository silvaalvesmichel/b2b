package br.com.b2b.infrastructure.messaging.publisher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.SnsException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SnsPublisherTest {

    @Mock
    private SnsClient snsClient;

    @InjectMocks
    private SnsPublisher snsPublisher;

    @Captor
    private ArgumentCaptor<PublishRequest> publishRequestCaptor;

    private final String TOPIC_ARN = "arn:aws:sns:us-east-1:000000000000:meu-topico";

    @Test
    void shouldPublishMessageSuccessfully() {
        // Arrange
        String message = "Hello, SNS!";

        // Act
        snsPublisher.publicar(message);

        // Assert
        // Verify that the snsClient.publish method was called once
        verify(snsClient).publish(publishRequestCaptor.capture());

        // Capture the request and assert its contents are correct
        PublishRequest capturedRequest = publishRequestCaptor.getValue();
        assertEquals(message, capturedRequest.message());
        assertEquals(TOPIC_ARN, capturedRequest.topicArn());
    }

    @Test
    void shouldPropagateExceptionWhenSnsClientFails() {
        // Arrange
        String message = "This message will fail.";
        SnsException snsException = (SnsException) SnsException.builder().message("AWS SNS Error").build();

        // Configure the mock to throw an exception when publish is called
        when(snsClient.publish(any(PublishRequest.class))).thenThrow(snsException);

        // Act & Assert
        // Verify that the SnsException is thrown by the 'publicar' method
        SnsException thrown = assertThrows(SnsException.class, () -> {
            snsPublisher.publicar(message);
        });

        // Optionally, check if the thrown exception is the one we expect
        assertEquals("AWS SNS Error", thrown.getMessage());

        // Verify that the publish method was still attempted
        verify(snsClient).publish(any(PublishRequest.class));
    }
}
