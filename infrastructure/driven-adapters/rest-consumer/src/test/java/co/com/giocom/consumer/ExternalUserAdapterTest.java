package co.com.giocom.consumer;

import co.com.giocom.model.user.User;
import co.com.giocom.model.user.gateways.ExternalUserGateway;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

class ExternalUserAdapterTest {

    private ExternalUserGateway externalUserRepository;

    private MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString()).build();

        externalUserRepository = new ExternalUserAdapter(webClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void shouldBeGetExternalUserById() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(
                "{\"data\":{\"id\":1,\"firstName\":\"Kevin\",\"lastName\":\"Mitnick\",\"email\":\"mitnick@example.com\",\"avatar\":\"avatar_url\"}}"));

        Mono<User> result = externalUserRepository.getById(1L);

        StepVerifier.create(result).expectNextMatches(
                user -> user.getId().equals(1L) && user.getFirstName()
                        .equals("Kevin") && user.getLastName()
                                .equals("Mitnick") && user.getEmail()
                                .equals("kevin@example.com") && user.getAvatar()
                                .equals("avatar_url")).verifyComplete();

        BDDMockito.verify(externalUserRepository).getById(1L);
    }
}