package co.com.giocom;

import co.com.giocom.model.UserDAO;
import co.com.giocom.model.user.User;
import co.com.giocom.repositories.R2dbcReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class UserReactiveR2dbcAdapterTest {

    @Mock
    private R2dbcReactiveRepository reactiveRepository;

    private R2dbcReactiveAdapter reactiveAdapter;

    private User user;

    private UserDAO userDao;

    @BeforeEach
    void setUp() {
        reactiveAdapter = new R2dbcReactiveAdapter(reactiveRepository);
        user = User.builder().id(1L).firstName("Kevin").lastName("Mitnick")
                .email("mitnick@mailinator.com").avatar("avatar").build();
        userDao = UserDAO.builder().id(1L).firstName("Kevin")
                .lastName("Mitnick").email("mitnick@mailinator.com")
                .avatar("avatar").build();
    }

    @Test
    void testSave() {
        BDDMockito.given(reactiveRepository.findById(1L))
                .willReturn(Mono.empty());
        BDDMockito.given(reactiveRepository.save(userDao))
                .willReturn(Mono.just(userDao));

        Mono<User> result = reactiveAdapter.save(user);

        StepVerifier.create(result).expectNextMatches(
                user -> user.getId().equals(1L) && user.getFirstName()
                        .equals("Kevin") && user.getLastName()
                                .equals("Mitnick") && user.getEmail()
                                .equals("mitnick@mailinator.com") && user.getAvatar()
                                .equals("avatar")).verifyComplete();
    }

    @Test
    void testGetUserById() {
        BDDMockito.given(reactiveRepository.findById(1L))
                .willReturn(Mono.just(userDao));

        Mono<User> result = reactiveAdapter.getById(1L);

        StepVerifier.create(result).expectNextMatches(
                user -> user.getId().equals(1L) && user.getFirstName()
                        .equals("Kevin") && user.getLastName()
                                .equals("Mitnick") && user.getEmail()
                                .equals("mitnick@mailinator.com") && user.getAvatar()
                                .equals("avatar")).verifyComplete();
    }

    @Test
    void testGetUserByName() {
        BDDMockito.given(
                        reactiveRepository.findByFirstNameIgnoreCaseStartingWith("k"))
                .willReturn(Flux.just(userDao));

        Flux<User> result = reactiveAdapter.getByName("k");

        StepVerifier.create(result).expectNextMatches(
                user -> user.getId().equals(1L) && user.getFirstName()
                        .equals("Kevin") && user.getLastName()
                                .equals("Mitnick") && user.getEmail()
                                .equals("mitnick@mailinator.com") && user.getAvatar()
                                .equals("avatar")).verifyComplete();
    }

    @Test
    void testFindAll() {
        BDDMockito.given(reactiveRepository.findAll())
                .willReturn(Flux.just(userDao));

        Flux<User> result = reactiveAdapter.findAll();

        StepVerifier.create(result).expectNextMatches(
                user -> user.getId().equals(1L) && user.getFirstName()
                        .equals("Kevin") && user.getLastName()
                                .equals("Mitnick") && user.getEmail()
                                .equals("mitnick@mailinator.com") && user.getAvatar()
                                .equals("avatar")).verifyComplete();
    }
}
