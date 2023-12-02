package co.com.giocom.model.user.gateways;

import co.com.giocom.model.user.User;
import reactor.core.publisher.Mono;

public interface UserSQSSenderGateway {

    Mono<String> sendUserCreated(User user);
}
