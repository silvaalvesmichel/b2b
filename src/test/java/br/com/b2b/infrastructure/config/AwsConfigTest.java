package br.com.b2b.infrastructure.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AwsConfigTest {

    private AwsConfig awsConfig;
    private SqsClient sqsClient;
    private SnsClient snsClient;

    @BeforeEach
    void setUp() {
        awsConfig = new AwsConfig();
    }

    @AfterEach
    void tearDown() {
        // It's good practice to close clients to release resources
        if (sqsClient != null) {
            sqsClient.close();
        }
        if (snsClient != null) {
            snsClient.close();
        }
    }

    @Test
    @DisplayName("Should create SqsClient bean successfully")
    void shouldCreateSqsClientBean() {
        // when
        sqsClient = awsConfig.sqsClient();

        // then
        assertNotNull(sqsClient, "SqsClient bean should not be null");
    }

    @Test
    @DisplayName("Should create SnsClient bean successfully")
    void shouldCreateSnsClientBean() {
        // when
        snsClient = awsConfig.snsClient();

        // then
        assertNotNull(snsClient, "SnsClient bean should not be null");
    }
}