package co.com.giocom.model.user.gateways;

import co.com.giocom.model.user.User;
import reactor.core.publisher.Mono;

public interface UserCacheGateway {

    Mono<User> getById(Long id);

    Mono<User> save(User user);
}
