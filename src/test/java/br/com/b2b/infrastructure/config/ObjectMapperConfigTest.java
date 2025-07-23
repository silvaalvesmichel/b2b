package br.com.b2b.infrastructure.config;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ObjectMapperConfigTest {

    private ObjectMapperConfig objectMapperConfig;

    @BeforeEach
    void setUp() {
        objectMapperConfig = new ObjectMapperConfig();
    }

    @Test
    @DisplayName("Should return a configured ObjectMapper")
    void shouldReturnConfiguredObjectMapper() throws JsonProcessingException {
        // when
        ObjectMapper objectMapper = objectMapperConfig.objectMapper();

        // then
        assertNotNull(objectMapper, "ObjectMapper should not be null");

        // Verify that WRITE_DATES_AS_TIMESTAMPS is disabled
        assertFalse(
                objectMapper.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS),
                "WRITE_DATES_AS_TIMESTAMPS should be disabled"
        );

        // Verify that JavaTimeModule is registered by serializing a LocalDateTime object
        LocalDateTime testDateTime = LocalDateTime.of(2023, 10, 27, 14, 30, 0);
        String json = objectMapper.writeValueAsString(testDateTime);

        // The expected output should be an ISO-8601 string
        assertEquals("\"2023-10-27T14:30:00\"", json, "LocalDateTime should be serialized to ISO-8601 format");
    }
}
