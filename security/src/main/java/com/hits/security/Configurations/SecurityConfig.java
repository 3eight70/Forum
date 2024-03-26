package com.hits.security.Configurations;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.hits.common.Consts.*;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(REGISTER_USER, LOGIN_USER).anonymous()
                        .requestMatchers(LOGOUT_USER).authenticated()
                        .requestMatchers(EDIT_CATEGORY, DELETE_CATEGORY).authenticated()
                        .requestMatchers(HttpMethod.POST, CREATE_CATEGORY).authenticated()
                        .requestMatchers(HttpMethod.POST, CREATE_THEME).authenticated()
                        .requestMatchers(EDIT_THEME, DELETE_THEME).authenticated()
                        .requestMatchers(SEND_MESSAGE, EDIT_MESSAGE, DELETE_MESSAGE).authenticated()
                        .requestMatchers(GET_FILES, UPLOAD_FILE).authenticated()
                        .requestMatchers(GET_FAVORITE, ADD_TO_FAVORITE, DELETE_FROM_FAVORITE).authenticated()
                        .requestMatchers(DOWNLOAD_FILE+"/*", DOWNLOAD_FILE + "*").authenticated()
                        .requestMatchers(BAN_USER, GIVE_MODERATOR, DELETE_MODERATOR).hasRole(ADMIN)
                        .anyRequest().permitAll())
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(accessDeniedHandler())
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private AccessDeniedHandler accessDeniedHandler(){
        return (httpServletRequest, httpServletResponse, e) -> {
            httpServletResponse.setContentType("application/json");
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            httpServletResponse.getWriter().write("{\"status\": \"403\", \"message\": \"" + "У вас нет прав доступа" + "\"}");
        };
    }
}
