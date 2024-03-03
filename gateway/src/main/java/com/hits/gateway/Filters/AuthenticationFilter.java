package com.hits.gateway.Filters;

import com.hits.gateway.Utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static com.hits.common.Consts.VALIDATE_TOKEN;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RouteValidator routeValidator;

    @Autowired
    private RestTemplate template;

    public AuthenticationFilter(){
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config){
        return (((exchange, chain) -> {
            ServerHttpRequest request = null;

            if (routeValidator.isSecured.test(exchange.getRequest())){
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                    throw new RuntimeException("Отсутствует header Authorization");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

                if (authHeader != null && authHeader.startsWith("Bearer ")){
                    authHeader = authHeader.substring(7);
                }

                try{
                    template.getForObject("http://USER-SERVICE" + VALIDATE_TOKEN + "?token=" + authHeader, String.class);

                    request = exchange.getRequest()
                            .mutate()
                            .header("Authorization", authHeader).build();
                }
                catch (Exception e){
                    throw new RuntimeException("Попытка получить доступ к приложение, неавторизованным пользователем");
                }
            }

            return chain.filter(exchange.mutate().request(request).build());
        }));
    }

    public static class Config {

    }
}
