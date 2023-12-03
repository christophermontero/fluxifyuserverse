package co.com.giocom.sqs.sender;

import co.com.giocom.model.user.User;
import co.com.giocom.model.user.gateways.UserSQSSenderGateway;
import co.com.giocom.sqs.sender.config.SQSSenderProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class SQSSenderAdapter implements UserSQSSenderGateway {

    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper objectMapper;

    public Mono<String> send(String message) {
        return Mono.fromCallable(() -> buildRequest(message)).flatMap(
                        request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.info("Message sent {}",
                        response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(String message) {
        return SendMessageRequest.builder().queueUrl(properties.getQueueUrl())
                .messageBody(message).build();
    }

    @Override
    public Mono<String> sendUserCreated(User user) {
        try {
            return this.send(objectMapper.writeValueAsString(user))
                    .doOnSubscribe(subs -> log.info(
                            "SQSSenderAdapter.sendUserCreated: {}",
                            Map.of("user", user)));
        } catch (JsonProcessingException e) {
            return Mono.error(new RuntimeException(e));
        }
    }
}
