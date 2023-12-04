package co.com.giocom.usecase.user;

import co.com.giocom.model.user.User;
import co.com.giocom.model.user.gateways.UserEventGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserEventUseCase {

    private final UserEventGateway userEventGateway;

    public Mono<User> saveEvent(User user) {
        return userEventGateway.save(user).thenReturn(user);
    }
}
