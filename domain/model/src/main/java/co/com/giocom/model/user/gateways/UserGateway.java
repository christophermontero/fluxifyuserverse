package co.com.giocom.model.user.gateways;

import co.com.giocom.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserGateway {

    Mono<User> save(User user);

    Mono<User> getById(Long id);

    Flux<User> getByName(String name);

    Flux<User> findAll();

}
