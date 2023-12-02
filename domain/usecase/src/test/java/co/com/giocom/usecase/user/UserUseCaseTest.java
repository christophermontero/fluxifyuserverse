package co.com.giocom.usecase.user;

import co.com.giocom.model.user.User;
import co.com.giocom.model.user.gateways.ExternalUserGateway;
import co.com.giocom.model.user.gateways.UserCacheGateway;
import co.com.giocom.model.user.gateways.UserGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {
    @Mock
    private UserGateway userGateway;

    @Mock
    private ExternalUserGateway externalUserGateway;

    @Mock
    private UserCacheGateway userCacheGateway;

    private UserUseCase userUseCase;

    private User user;

    private List<User> userList = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        userUseCase = new UserUseCase(userGateway, externalUserGateway,
                userCacheGateway, userSQSGateway);
        user = User.builder().id(1L).firstName("Kevin").lastName("Mitnick")
                .email("mitnick@mailinator.com").avatar("avatar").build();
        userList = Arrays.asList(
                User.builder().id(1L).firstName("Kevin").lastName("Smith")
                        .email("kevin@mail.com").avatar("avatar1").build(),
                User.builder().id(2L).firstName("Keith").lastName("Johnson")
                        .email("keith@mail.com").avatar("avatar2").build());
    }

    @Test
    void shouldBeCreateUserById() {
        BDDMockito.given(externalUserGateway.getById(1L))
                .willReturn(Mono.just(user));
        BDDMockito.given(userGateway.save(user)).willReturn(Mono.just(user));
        BDDMockito.given(userSQSGateway.sendUserCreated(user))
                .willReturn(Mono.just("123456789"));

        Mono<User> result = userUseCase.createById(1L);

        StepVerifier.create(result).expectNext(user).verifyComplete();

        BDDMockito.verify(externalUserGateway).getById(1L);
        BDDMockito.verify(userGateway).save(user);
        BDDMockito.verify(userSQSGateway).sendUserCreated(user);
    }

    @Test
    void shouldBeGetUserByIdFromCache() {
        BDDMockito.given(userCacheGateway.getById(1L))
                .willReturn(Mono.just(user));

        Mono<User> result = userUseCase.getById(1L);

        StepVerifier.create(result).expectNext(user).verifyComplete();

        BDDMockito.verify(userCacheGateway).getById(1L);
        BDDMockito.verify(userGateway, Mockito.times(0)).getById(1L);
        BDDMockito.verify(userCacheGateway, Mockito.times(0)).save(user);
    }

    @Test
    void shouldBeGetUserByIdIfNotExistsInCache() {
        BDDMockito.given(userCacheGateway.getById(1L)).willReturn(Mono.empty());
        BDDMockito.given(userGateway.getById(1L)).willReturn(Mono.just(user));
        BDDMockito.given(userCacheGateway.save(user))
                .willReturn(Mono.just(user));

        Mono<User> result = userUseCase.getById(1L);

        StepVerifier.create(result).expectNext(user).verifyComplete();

        BDDMockito.verify(userGateway).getById(1L);
        BDDMockito.verify(userCacheGateway).getById(1L);
        BDDMockito.verify(userCacheGateway).save(user);
    }

    @Test
    void shouldBeGetUsersByName() {
        BDDMockito.given(userGateway.getByName("k"))
                .willReturn(Flux.fromIterable(userList));

        Flux<User> result = userUseCase.getByName("k");

        StepVerifier.create(result).expectNext(userList.get(0))
                .expectNext(userList.get(1)).verifyComplete();

        BDDMockito.verify(userGateway).getByName("k");
    }

    @Test
    void shouldBeFindAllUsers() {
        BDDMockito.given(userGateway.findAll())
                .willReturn(Flux.fromIterable(userList));

        Flux<User> result = userUseCase.findAll();

        StepVerifier.create(result).expectNext(userList.get(0))
                .expectNext(userList.get(1)).verifyComplete();

        BDDMockito.verify(userGateway).findAll();
    }
}
