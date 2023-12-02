package co.com.giocom.consumer;

import co.com.giocom.consumer.dto.ExternalDataResponse;
import co.com.giocom.model.user.User;
import co.com.giocom.model.user.gateways.ExternalUserGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class RestConsumerAdapter implements ExternalUserGateway {
    private final WebClient webClient;

    private static User mapToUser(ExternalDataResponse res) {
        return User.builder().id(res.getData().getId())
                .firstName(res.getData().getFirstName())
                .lastName(res.getData().getLastName())
                .email(res.getData().getEmail())
                .avatar(res.getData().getAvatar()).build();
    }

    @Override
    public Mono<User> getById(Long id) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/{id}").build(id))
                .retrieve().bodyToMono(ExternalDataResponse.class)
                .map(RestConsumerAdapter::mapToUser);
    }
}
