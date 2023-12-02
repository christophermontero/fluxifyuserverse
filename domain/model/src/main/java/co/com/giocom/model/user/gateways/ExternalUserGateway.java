package co.com.giocom.model.user.gateways;

import co.com.giocom.model.user.User;
import reactor.core.publisher.Mono;

public interface ExternalUserGateway {

    Mono<User> getById(Long id);
}
