package com.hits.gateway.Filters;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

import static com.hits.common.Consts.*;

@Component
public class RouteValidator {
    public static final List<String> apiEndpoints = List.of(DOWNLOAD_FILE,
            GET_FILES,
            UPLOAD_FILE,
            EUREKA,
            LOGOUT_USER);

    public Predicate<ServerHttpRequest> isSecured =
            request -> apiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));
}
