package com.hits.user.Configurations;

import com.hits.common.Models.User.UserDto;
import com.hits.user.Models.Entities.User;
import com.hits.user.Repositories.RedisRepository;
import com.hits.user.Repositories.UserRepository;
import com.hits.user.Utils.JwtTokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private final RedisRepository redisRepository;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String login = null;
        String jwt = null;
        boolean tokenInRedis = false;

        response.setHeader("Access-Control-Allow-Origin", "http://localhost");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With, X-Auth-Token");

        if (authHeader != null && authHeader.equals("Bearer null")) {
            authHeader = null;
        }
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);

                if (redisRepository.checkToken(jwtTokenUtils.getIdFromToken(jwt))) {
                    tokenInRedis = true;
                }
                login = jwtTokenUtils.getUserLogin(jwt);
            }

            User user = userRepository.findByLogin(login);
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setEmail(user.getEmail());
            userDto.setLogin(user.getLogin());
            userDto.setCreateTime(user.getCreateTime());
            userDto.setAuthorities(user.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));


            if (login != null && SecurityContextHolder.getContext().getAuthentication() == null && tokenInRedis && user != null) {
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                        userDto,
                        null,
                        userDto.getAuthorities()
                                .stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList())
                );
                SecurityContextHolder.getContext().setAuthentication(token);
            }
        }
        catch (UsernameNotFoundException | ExpiredJwtException | SignatureException e){

        }
        if (request.getMethod().equals("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
