package co.com.giocom.repositories;

import co.com.giocom.model.UserDAO;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface R2dbcReactiveRepository
        extends ReactiveCrudRepository<UserDAO, Long>,
        ReactiveQueryByExampleExecutor<UserDAO> {

    Flux<UserDAO> findByFirstNameIgnoreCaseStartingWith(String name);
}
