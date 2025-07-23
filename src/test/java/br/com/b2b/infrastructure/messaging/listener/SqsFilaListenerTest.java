package br.com.b2b.infrastructure.messaging.listener;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SqsFilaListenerTest {

    // A stream to capture the output from System.out
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private SqsFilaListener sqsFilaListener;

    @BeforeEach
    void setUp() {
        // Instantiate the class to be tested
        sqsFilaListener = new SqsFilaListener();
        // Redirect System.out to our stream
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        // Restore the original System.out stream
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Should process message and print it to the console")
    void shouldProcessMessageAndPrintToConsole() {
        // Given
        String testMessage = "Hello from the test!";
        String expectedOutput = "📥 Recebido da fila: " + testMessage + System.lineSeparator();

        // When
        sqsFilaListener.processarMensagem(testMessage);

        // Then
        // Assert that the captured output matches the expected string
        assertEquals(expectedOutput, outContent.toString());
    }
}
