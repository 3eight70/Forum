package com.hits.gateway.Filters;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

import static com.hits.common.Consts.*;

@Component
public class RouteValidator {
    public static final List<String> apiEndpoints = List.of(
            LOGIN_USER,
            REGISTER_USER,
            REFRESH_TOKEN,
            EUREKA);

    public Predicate<ServerHttpRequest> isSecured =
            request -> apiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));
}
