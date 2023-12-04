package co.com.giocom.usecase.user;

import co.com.giocom.model.user.User;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserEventUseCase {

    public Mono<User> saveEvent(User user) {
        return Mono.just(user);
    }
}
