package com.hits.security.Rest.Configurations;

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

import static com.hits.common.Core.Consts.*;

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
                        .requestMatchers(EDIT_CATEGORY, DELETE_CATEGORY).hasRole(ADMIN)
                        .requestMatchers(HttpMethod.POST, CREATE_CATEGORY).hasRole(ADMIN)
                        .requestMatchers(HttpMethod.POST, CREATE_THEME).authenticated()
                        .requestMatchers(EDIT_THEME).authenticated()
                        .requestMatchers(DELETE_THEME).hasAnyRole(ADMIN, MODERATOR)
                        .requestMatchers(SEND_MESSAGE, EDIT_MESSAGE, DELETE_MESSAGE).authenticated()
                        .requestMatchers(GET_FILES).authenticated()
                        .requestMatchers(GET_FAVORITE,
                                ADD_TO_FAVORITE,
                                DELETE_FROM_FAVORITE).authenticated()
                        .requestMatchers(DOWNLOAD_FILE+"/*", DOWNLOAD_FILE + "*").authenticated()
                        .requestMatchers(UPLOAD_FILE, DELETE_FILE).authenticated()
                        .requestMatchers(BAN_USER,
                                GIVE_MODERATOR,
                                DELETE_MODERATOR,
                                GIVE_CATEGORY,
                                CREATE_USER,
                                EDIT_USER).hasRole(ADMIN)
                        .requestMatchers(ARCHIVE_THEME).hasAnyRole(ADMIN, MODERATOR)
                        .requestMatchers(GET_PROFILE).authenticated()
                        .requestMatchers(DELETE_ATTACHMENT).authenticated()
                        .requestMatchers(GET_NOTIFICATIONS,
                                GET_UNREAD_NOTIFICATIONS,
                                READ_ALL_NOTIFICATIONS,
                                READ_NOTIFICATION).authenticated()
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
