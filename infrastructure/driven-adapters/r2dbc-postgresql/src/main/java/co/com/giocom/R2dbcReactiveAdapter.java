package co.com.giocom;

import co.com.giocom.model.UserDAO;
import co.com.giocom.model.user.User;
import co.com.giocom.model.user.gateways.UserGateway;
import co.com.giocom.repositories.R2dbcReactiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class R2dbcReactiveAdapter implements UserGateway {

    private final R2dbcReactiveRepository reactiveRepository;

    @Override
    public Mono<User> save(User user) {
        return reactiveRepository.findById(user.getId())
                .switchIfEmpty( // Find user by id if not exists create new one
                        reactiveRepository.save(
                                UserDAO.builder().id(user.getId())
                                        .firstName(user.getFirstName())
                                        .lastName(user.getLastName())
                                        .email(user.getEmail())
                                        .avatar(user.getAvatar()).build()))
                .map(usr -> User.builder().id(usr.getId())
                        .firstName(usr.getFirstName())
                        .lastName(usr.getLastName()).email(usr.getEmail())
                        .avatar(usr.getAvatar()).build());
    }

    @Override
    public Mono<User> getById(Long id) {
        return reactiveRepository.findById(id)
                .map(user -> User.builder().id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName()).email(user.getEmail())
                        .avatar(user.getAvatar()).build());
    }

    @Override
    public Flux<User> getByName(String name) {
        return reactiveRepository.findByFirstNameIgnoreCaseStartingWith(name)
                .map(user -> User.builder().id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName()).email(user.getEmail())
                        .avatar(user.getAvatar()).build());
    }

    @Override
    public Flux<User> findAll() {
        return reactiveRepository.findAll()
                .map(user -> User.builder().id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName()).email(user.getEmail())
                        .avatar(user.getAvatar()).build());
    }
}
