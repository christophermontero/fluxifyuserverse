package co.com.giocom.sqs.sender.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "adapter.sqs")
public class SQSSenderProperties {

    private String region;
    private String queueUrl;
    private String endpoint;
}
