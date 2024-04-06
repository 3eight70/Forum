package com.hits.security.Rest.Configurations;

import feign.RequestInterceptor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Setter
@Configuration
public class FeignClientConfiguration {

    private boolean authenticationRequired = true;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authenticationRequired && authentication != null && authentication.isAuthenticated()) {
                template.header("Authorization", "Bearer " + authentication.getCredentials());
            }
        };
    }
}
