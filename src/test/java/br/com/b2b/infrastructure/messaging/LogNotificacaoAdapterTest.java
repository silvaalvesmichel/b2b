package br.com.b2b.infrastructure.messaging;


import br.com.b2b.domain.model.Pedido;
import br.com.b2b.domain.model.enums.StatusPedido;
import br.com.b2b.infrastructure.messaging.publisher.SnsPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LogNotificacaoAdapterTest {

    @Mock
    private SnsPublisher snsPublisher;

    @InjectMocks
    private LogNotificacaoAdapter logNotificacaoAdapter;

    @Captor
    private ArgumentCaptor<String> jsonCaptor;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Configure ObjectMapper exactly as in the class under test to ensure consistent JSON
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    @DisplayName("Should successfully serialize and publish a status change notification")
    void notificarMudancaStatus_shouldSerializeAndPublishNotification() throws JsonProcessingException {
        // Given
        UUID pedidoId = UUID.randomUUID();
        Pedido pedido = Pedido.builder()
                .id(pedidoId)
                .idParceiro(12345L)
                .status(StatusPedido.APROVADO)
                .itens(Collections.emptyList())
                .dataCriacao(LocalDateTime.now().minusDays(1))
                .dataUltimaAtualizacao(LocalDateTime.now())
                .build();

        String expectedJson = objectMapper.writeValueAsString(pedido);

        // When
        logNotificacaoAdapter.notificarMudancaStatus(pedido);

        // Then
        // Verify that the publisher was called exactly once
        verify(snsPublisher, times(1)).publicar(jsonCaptor.capture());

        String actualJson = jsonCaptor.getValue();

        // Assert that the captured JSON is what we expect
        assertEquals(expectedJson, actualJson);

        // For extra safety, we can also check for the presence of key fields
        assertTrue(actualJson.contains(pedidoId.toString()));
        assertTrue(actualJson.contains("\"idParceiro\":12345"));
        assertTrue(actualJson.contains("\"status\":\"APROVADO\""));
    }
}
