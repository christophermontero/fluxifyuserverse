package co.com.giocom.api;

import co.com.giocom.api.dto.UserRequest;
import co.com.giocom.api.dto.UserResponse;
import co.com.giocom.model.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
@WebFluxTest
@ContextConfiguration(classes = { RouterRest.class, Handler.class })
class HandlerTest {

    @Autowired
    private ApplicationContext context;

    private WebTestClient webTestClient;

    private ObjectMapper mapper;

    private User user;

    private UserRequest userReq;

    private List<User> userList = new ArrayList<>();

    @MockBean
    private UserUseCase userUseCase;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        webTestClient = WebTestClient.bindToApplicationContext(context)
                .configureClient().build();
        user = User.builder().id(1L).firstName("Kevin").lastName("Mitnick")
                .email("mitnick@mailinator.com").avatar("avatar").build();
        userReq = UserRequest.builder().id(1L).build();
        userList = Arrays.asList(
                User.builder().id(1L).firstName("Kevin").lastName("Smith")
                        .email("kevin@mail.com").avatar("avatar1").build(),
                User.builder().id(2L).firstName("Keith").lastName("Johnson")
                        .email("keith@mail.com").avatar("avatar2").build());
    }

    @Test
    void testCreateById() {
        BDDMockito.given(userUseCase.createById(1L))
                .willReturn(Mono.just(user));

        webTestClient.post().uri("/api/v1/users")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(userReq), UserRequest.class).exchange()
                .expectStatus().isOk().expectBody(UserResponse.class)
                .value(res -> {
                    Assertions.assertEquals(user.getId(), res.getId());
                    Assertions.assertEquals(user.getFirstName(),
                            res.getFirstName());
                    Assertions.assertEquals(user.getLastName(),
                            res.getLastName());
                    Assertions.assertEquals(user.getEmail(), res.getEmail());
                    Assertions.assertEquals(user.getAvatar(), res.getAvatar());
                });

        BDDMockito.verify(userUseCase).createById(1L);
    }

    @Test
    void testGetById() {
        BDDMockito.given(userUseCase.getById(1L)).willReturn(Mono.just(user));

        webTestClient.get().uri("/api/v1/users/{id}", 1)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus()
                .isOk().expectBody(UserResponse.class).value(res -> {
                    Assertions.assertEquals(user.getId(), res.getId());
                    Assertions.assertEquals(user.getFirstName(), res.getFirstName());
                    Assertions.assertEquals(user.getLastName(), res.getLastName());
                    Assertions.assertEquals(user.getEmail(), res.getEmail());
                    Assertions.assertEquals(user.getAvatar(), res.getAvatar());
                });

        BDDMockito.verify(userUseCase).getById(1L);
    }

    @Test
    void testGetByIdWhenNotExists() {
        BDDMockito.given(userUseCase.getById(1L)).willReturn(Mono.empty());

        webTestClient.get().uri("/api/v1/users/{id}", 1)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus()
                .isNotFound();
    }

    @Test
    void testGetByName() {
        List<UserResponse> userResList = userList.stream()
                .map(usr -> UserResponse.builder().id(usr.getId())
                        .firstName(usr.getFirstName())
                        .lastName(usr.getLastName()).email(usr.getEmail())
                        .avatar(usr.getAvatar()).build())
                .collect(Collectors.toList());

        BDDMockito.given(userUseCase.getByName("k"))
                .willReturn(Flux.fromIterable(userList));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/api/v1/users")
                        .queryParam("name", "k").build())
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus()
                .isOk().expectBodyList(UserResponse.class).value(res -> {
                    MatcherAssert.assertThat(res, CoreMatchers.is(userResList));
                    MatcherAssert.assertThat(res, Matchers.hasSize(2));
                });

        BDDMockito.verify(userUseCase).getByName("k");
    }

    @Test
    void testGetByNameWithEmptyName() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/api/v1/users")
                        .queryParam("name", "").build())
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus()
                .isBadRequest();
    }

    @Test
    void testFindAll() {
        List<UserResponse> userResList = userList.stream()
                .map(usr -> UserResponse.builder().id(usr.getId())
                        .firstName(usr.getFirstName())
                        .lastName(usr.getLastName()).email(usr.getEmail())
                        .avatar(usr.getAvatar()).build())
                .collect(Collectors.toList());

        BDDMockito.given(userUseCase.findAll())
                .willReturn(Flux.fromIterable(userList));

        webTestClient.get().uri("/api/v1/users")
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus()
                .isOk().expectBodyList(UserResponse.class).value(res -> {
                    MatcherAssert.assertThat(res, CoreMatchers.is(userResList));
                    MatcherAssert.assertThat(res, Matchers.hasSize(2));
                });

        BDDMockito.verify(userUseCase).findAll();
    }
}
