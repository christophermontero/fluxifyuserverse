package co.com.giocom.redis.template;

import co.com.giocom.model.user.User;
import co.com.giocom.redis.template.model.UserCacheDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class RedisReactiveAdapterTest {

    @Mock
    ReactiveRedisTemplate<String, UserCacheDAO> reactiveRedisTemplate;
    @Mock
    ReactiveValueOperations<String, UserCacheDAO> reactiveValueOperations;
    @InjectMocks
    private RedisReactiveAdapter reactiveRedisAdapter;
    @Mock
    private ReactiveRedisConnectionFactory connectionFactory;
    @Mock
    private ObjectMapper objectMapper;
    private User user;

    private UserCacheDAO userCache;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(reactiveRedisAdapter, "mapper",
                objectMapper);
        ReflectionTestUtils.setField(reactiveRedisAdapter, "template",
                reactiveRedisTemplate);
        BDDMockito.given(reactiveRedisTemplate.opsForValue())
                .willReturn(reactiveValueOperations);

        user = User.builder().id(1L).firstName("Kevin").lastName("Mitnick")
                .email("mitnick@mailinator.com").avatar("avatar").build();
        userCache = UserCacheDAO.builder().id(1L).firstName("Kevin")
                .lastName("Mitnick").email("mitnick@mailinator.com")
                .avatar("avatar").build();
    }

    @Test
    void shouldBeSaveUserInCache() {
        BDDMockito.given(
                        reactiveRedisTemplate.opsForValue().set(anyString(), any()))
                .willReturn(Mono.just(true));
        BDDMockito.given(reactiveRedisTemplate.expire(anyString(), any()))
                .willReturn(Mono.just(true));
        BDDMockito.given(objectMapper.map(any(), any())).willReturn(userCache);

        Mono<User> result = reactiveRedisAdapter.save(user);

        StepVerifier.create(result).expectNext(user).verifyComplete();
    }

    @Test
    void shouldBeGetUserByIdFromCache() {
        BDDMockito.given(reactiveRedisTemplate.opsForValue().get(anyString()))
                .willReturn(Mono.just(userCache));
        BDDMockito.given(objectMapper.map(any(), any())).willReturn(user);

        Mono<User> result = reactiveRedisAdapter.getById(1L);

        StepVerifier.create(result).expectNext(user).verifyComplete();
    }
}