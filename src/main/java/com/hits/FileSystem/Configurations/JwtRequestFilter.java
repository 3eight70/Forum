package com.hits.FileSystem.Configurations;

import com.hits.FileSystem.Models.Entity.User;
import com.hits.FileSystem.Repositories.RedisRepository;
import com.hits.FileSystem.Services.IUserService;
import com.hits.FileSystem.Utils.JwtTokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private final RedisRepository redisRepository;
    private final IUserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String email = null;
        String jwt = null;
        boolean tokenInRedis = false;

        response.setHeader("Access-Control-Allow-Origin", "http://localhost");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With, X-Auth-Token");

        if (authHeader != null && authHeader.equals("Bearer null")) {
            authHeader = null;
        }

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);

            if (redisRepository.checkToken(jwtTokenUtils.getIdFromToken(jwt))) {
                tokenInRedis = true;
            }
            email = jwtTokenUtils.getUserEmail(jwt);
        }

        try {
            User user = userService.loadUserByUsername(email);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null && tokenInRedis && user != null) {
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(token);
            }
        }
        catch (UsernameNotFoundException e){

        }
        if (request.getMethod().equals("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
