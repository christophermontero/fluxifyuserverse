package co.com.giocom;

import co.com.giocom.model.UserDAO;
import co.com.giocom.model.user.User;
import co.com.giocom.model.user.gateways.UserGateway;
import co.com.giocom.repositories.R2dbcReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
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
                        .avatar(usr.getAvatar()).build()).doOnSubscribe(
                        subs -> log.info("R2dbcReactiveAdapter.save: {}",
                                Map.of("user", user))).doOnError(
                        error -> log.error("Error when was saving user: {}",
                                Map.of("SaveError", error.getMessage())));
    }

    @Override
    public Mono<User> getById(Long id) {
        return reactiveRepository.findById(id)
                .map(user -> User.builder().id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName()).email(user.getEmail())
                        .avatar(user.getAvatar()).build()).doOnSubscribe(
                        subs -> log.info("R2dbcReactiveAdapter.getById: {}",
                                Map.of("id", id))).doOnError(error -> log.error(
                        "Error when was fetching user by id: {}",
                        Map.of("GetByIdError", error.getMessage())));
    }

    @Override
    public Flux<User> getByName(String name) {
        return reactiveRepository.findByFirstNameIgnoreCaseStartingWith(name)
                .map(user -> User.builder().id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName()).email(user.getEmail())
                        .avatar(user.getAvatar()).build()).doOnSubscribe(
                        subs -> log.info("R2dbcReactiveAdapter.getByName: {}",
                                Map.of("name", name))).doOnError(
                        error -> log.error(
                                "Error when was fetching user by name: {}",
                                Map.of("GetByNameError", error.getMessage())));
    }

    @Override
    public Flux<User> findAll() {
        return reactiveRepository.findAll()
                .map(user -> User.builder().id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName()).email(user.getEmail())
                        .avatar(user.getAvatar()).build())
                .doOnSubscribe(subs -> log.info("R2dbcReactiveAdapter.findAll"))
                .doOnError(error -> log.error(
                        "Error when was fetching all users: {}",
                        Map.of("FindAllError", error.getMessage())));
    }
}
