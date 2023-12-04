package co.com.giocom.dynamodb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

import java.net.URI;

@Configuration
public class DynamoDBConfig {

    @Bean
    public DynamoDbAsyncClient amazonDynamoDB(
            @Value("${adapter.dynamodb.endpoint}") String endpoint,
            @Value("${adapter.dynamodb.region}") String region,
            MetricPublisher publisher) {
        return DynamoDbAsyncClient.builder().credentialsProvider(
                        ProfileCredentialsProvider.create("default"))
                .endpointOverride(URI.create(endpoint))
                .overrideConfiguration(o -> o.addMetricPublisher(publisher))
                .region(Region.of(region)).build();
    }

    @Bean
    public DynamoDbEnhancedAsyncClient getDynamoDbEnhancedAsyncClient(
            DynamoDbAsyncClient client) {
        return DynamoDbEnhancedAsyncClient.builder().dynamoDbClient(client)
                .build();
    }

}
