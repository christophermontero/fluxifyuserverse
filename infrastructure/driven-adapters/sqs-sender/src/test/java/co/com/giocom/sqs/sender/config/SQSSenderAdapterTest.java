package co.com.giocom.sqs.sender.config;

import co.com.giocom.model.user.User;
import co.com.giocom.sqs.sender.SQSSenderAdapter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SQSSenderAdapterTest {

    @Mock
    private SqsAsyncClient client;

    @Mock
    private ObjectMapper objectMapper;

    private SQSSenderAdapter senderAdapter;

    private User user;

    @BeforeEach
    void setUp() {
        SQSSenderProperties properties = new SQSSenderProperties();
        properties.setRegion("us-east-1");
        properties.setEndpoint("http://localhost:4566");
        properties.setQueueUrl(
                "http://localhost:4566/000000000000/user-created-queue");

        user = User.builder().id(1L).firstName("Kevin").lastName("Mitnick")
                .email("mitnick@mailinator.com").avatar("avatar").build();

        senderAdapter = new SQSSenderAdapter(properties, client, objectMapper);
    }

    @Test
    void testSendUserCreated() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        SendMessageResponse sendMessageResponse = SendMessageResponse.builder()
                .messageId("123456789").build();

        when(client.sendMessage(any(SendMessageRequest.class))).thenReturn(
                CompletableFuture.supplyAsync(() -> sendMessageResponse));

        when(objectMapper.writeValueAsString(any(User.class))).thenReturn(
                mapper.writeValueAsString(user));

        Mono<String> result = senderAdapter.sendUserCreated(user);

        StepVerifier.create(result).expectNext("123456789").verifyComplete();
    }

    @Test
    void shouldThrowExceptionWhenCreateUserJsonObject()
            throws JsonProcessingException {

        when(objectMapper.writeValueAsString(any())).thenThrow(
                JsonProcessingException.class);

        Mono<String> userCreatedEvent = senderAdapter.sendUserCreated(null);

        StepVerifier.create(userCreatedEvent).expectError().verify();
    }
}
