package co.com.giocom.sqs.listener.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "entrypoint.sqs")
public class SQSProperties {

    private String region;
    private String queueUrl;
    private String endpoint;
    private int waitTimeSeconds;
    private int maxNumberOfMessages;
    private int visibilityTimeout;
    private int numberOfThreads;
}
