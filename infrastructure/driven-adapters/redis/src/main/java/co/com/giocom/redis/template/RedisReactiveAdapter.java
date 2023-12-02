package co.com.giocom.redis.template;

import co.com.giocom.model.user.User;
import co.com.giocom.model.user.gateways.UserCacheGateway;
import co.com.giocom.redis.template.helper.ReactiveTemplateAdapterOperations;
import co.com.giocom.redis.template.model.UserCacheDAO;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class RedisReactiveAdapter
        extends ReactiveTemplateAdapterOperations<User, String, UserCacheDAO>
        implements UserCacheGateway {

    private static final long EXPIRATION_MILLIS = 900000; // Expiration time is set in 15 min

    public RedisReactiveAdapter(
            ReactiveRedisConnectionFactory connectionFactory,
            ObjectMapper mapper) {
        super(connectionFactory, mapper, d -> mapper.map(d, User.class));
    }

    @Override
    public Mono<User> getById(Long id) {
        return findById(String.valueOf(buildKey(id)));
    }

    @Override
    public Mono<User> save(User user) {
        return save(String.valueOf(buildKey(user.getId())), user,
                EXPIRATION_MILLIS);
    }

    private StringBuilder buildKey(Long id) {
        return new StringBuilder("user:").append(id.hashCode());
    }

}
