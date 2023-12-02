package co.com.giocom.api;

import co.com.giocom.api.dto.UserRequest;
import co.com.giocom.api.dto.UserResponse;
import co.com.giocom.model.user.User;
import co.com.giocom.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final UserUseCase userUseCase;

    private static UserResponse mapToUserRes(User user) {
        return UserResponse.builder().id(user.getId())
                .firstName(user.getFirstName()).lastName(user.getLastName())
                .email(user.getEmail()).avatar(user.getAvatar()).build();
    }

    public Mono<ServerResponse> createById(UserRequest userReq) {
        return userUseCase.createById(userReq.getId())
                .map(Handler::mapToUserRes).flatMap(
                        userRes -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(userRes));
    }

    public Mono<ServerResponse> getById(ServerRequest serverRequest) {
        String userId = serverRequest.pathVariable(
                "id"); // Extract id from uri path
        return userUseCase.getById(Long.valueOf(userId))
                .map(Handler::mapToUserRes).flatMap(
                        userRes -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(userRes)).switchIfEmpty(
                        ServerResponse.notFound()
                                .build()); // If id not exists return not found
    }

    public Mono<ServerResponse> getByName(String name) {
        return ServerResponse.ok()
                .body(userUseCase.getByName(name).map(Handler::mapToUserRes),
                        UserResponse.class);
    }

    public Mono<ServerResponse> findAll() {
        return ServerResponse.ok()
                .body(userUseCase.findAll().map(Handler::mapToUserRes),
                        UserResponse.class);
    }

    public Mono<ServerResponse> fetch(ServerRequest serverRequest) {
        return Mono.fromSupplier(() -> serverRequest.queryParam(
                        "name")) // Get the query param if exists else return empty Mono
                .flatMap(name -> {
                    if (name.isPresent()) {
                        int nameValue = name.get().length();
                        if (nameValue > 0) { // If query param is greater than 0 call getByName method
                            return getByName(name.get());
                        } else {
                            return ServerResponse.badRequest().build();
                        }
                    } else {
                        return findAll(); // Otherwise call findAll
                    }
                });
    }
}
