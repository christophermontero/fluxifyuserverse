package co.com.giocom.usecase.user;

import co.com.giocom.model.user.User;
import co.com.giocom.model.user.gateways.UserGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserGateway userGateway;

    public Mono<User> createById(Long id) {
        return externalUserGateway.getById(id).flatMap(
                user -> userGateway.save(user).flatMap(
                        msg -> userSQSGateway.sendUserCreated(msg)
                                .thenReturn(msg)));
    }

    public Mono<User> getById(Long id) {
        return userCacheGateway.getById(id).switchIfEmpty(Mono.defer(
                () -> userGateway.getById(id).flatMap(userCacheGateway::save)));
    }

    public Flux<User> getByName(String name) {
        return userGateway.getByName(name);
    }

    public Flux<User> findAll() {
        return userGateway.findAll();
    }

}
