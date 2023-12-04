package co.com.giocom.sqs.listener;

import co.com.giocom.model.user.User;
import co.com.giocom.usecase.user.UserEventUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.Objects;
import java.util.function.Function;

@Slf4j
@Service
@AllArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {

    private final UserEventUseCase userEventUseCase;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> apply(Message message) {
        log.info("Message received {}", message.body());
        try {
            User user = Objects.requireNonNull(objectMapper)
                    .readValue(message.body(), User.class);
            return Objects.requireNonNull(userEventUseCase).saveEvent(user)
                    .then();
        } catch (Exception e) {
            return Mono.error(new RuntimeException(e));
        }
    }
}
