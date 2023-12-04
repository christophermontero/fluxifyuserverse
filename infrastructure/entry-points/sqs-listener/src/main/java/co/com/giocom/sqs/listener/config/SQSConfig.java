package co.com.giocom.sqs.listener.config;

import co.com.giocom.sqs.listener.helper.SQSListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.Message;

import java.net.URI;
import java.util.function.Function;

@Configuration
public class SQSConfig {

    @Bean
    public SQSListener sqsListener(SqsAsyncClient client,
            SQSProperties properties, Function<Message, Mono<Void>> fn) {
        return SQSListener.builder().client(client).properties(properties)
                .processor(fn).build().start();
    }

    @Bean
    public SqsAsyncClient configSqs(SQSProperties properties,
            MetricPublisher publisher) {
        return SqsAsyncClient.builder()
                .endpointOverride(resolveEndpoint(properties))
                .region(Region.of(properties.getRegion()))
                .overrideConfiguration(o -> o.addMetricPublisher(publisher))
                .credentialsProvider(getProviderChain()).build();
    }

    private AwsCredentialsProviderChain getProviderChain() {
        return AwsCredentialsProviderChain.builder().addCredentialsProvider(
                        EnvironmentVariableCredentialsProvider.create())
                .addCredentialsProvider(
                        SystemPropertyCredentialsProvider.create())
                .addCredentialsProvider(
                        WebIdentityTokenFileCredentialsProvider.create())
                .addCredentialsProvider(ProfileCredentialsProvider.create())
                .addCredentialsProvider(
                        ContainerCredentialsProvider.builder().build())
                .addCredentialsProvider(
                        InstanceProfileCredentialsProvider.create()).build();
    }

    private URI resolveEndpoint(SQSProperties properties) {
        if (properties.getEndpoint() != null) {
            return URI.create(properties.getEndpoint());
        }
        return null;
    }
}
