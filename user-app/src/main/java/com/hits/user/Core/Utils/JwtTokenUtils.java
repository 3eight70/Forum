package com.hits.user.Core.Utils;


import com.hits.user.Core.User.Entity.User;
import com.hits.user.Core.User.Repository.RedisRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {
    private final RedisRepository redisRepository;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Duration lifetime;

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        List<String> rolesList = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        claims.put("roles", rolesList);

        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + lifetime.toMillis());
        UUID tokenId = UUID.randomUUID();

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getLogin())
                .claim("userId", user.getId().toString())
                .setId(tokenId.toString())
                .setIssuedAt(issuedDate)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();
    }

    public String getUserLogin(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    public void saveToken(String key, String value) {
        redisRepository.save(key, value, lifetime.toMillis());
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean validateToken(String token){
        return redisRepository.checkToken(getIdFromToken(token));
    }

    public String getIdFromToken(String token) {
        return getAllClaimsFromToken(token).getId();
    }
}
