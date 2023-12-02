package co.com.giocom.api;

import co.com.giocom.api.dto.UserRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler userHandler) {
        return route(POST("/api/v1/users"),
                userReq -> userReq.bodyToMono(UserRequest.class)
                        .flatMap(userHandler::createById)).andRoute(
                        GET("/api/v1/users/{id}"), userHandler::getById)
                .andRoute(GET("/api/v1/users"), userHandler::fetch);
    }
}
