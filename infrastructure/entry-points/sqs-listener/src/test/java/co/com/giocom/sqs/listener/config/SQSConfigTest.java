package co.com.giocom.sqs.listener.config;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.metrics.LoggingMetricPublisher;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SQSConfigTest {

    @Mock
    private SqsAsyncClient asyncClient;

    @Test
    void configTest() {
        SQSProperties sqsProperties = new SQSProperties();
        sqsProperties.setNumberOfThreads(1);
        sqsProperties.setRegion("Region");

        LoggingMetricPublisher loggingMetricPublisher = LoggingMetricPublisher.create();

        SQSConfig sqsConfig = new SQSConfig();

        assertNotNull(sqsConfig.sqsListener(asyncClient, sqsProperties,
                message -> Mono.empty()));
        assertNotNull(
                sqsConfig.configSqs(sqsProperties, loggingMetricPublisher));

    }
}