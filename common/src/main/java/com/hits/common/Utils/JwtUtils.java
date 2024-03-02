package com.hits.common.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

    public static String getUserEmail(String token, String secret) {
        return getAllClaimsFromToken(token, secret).getSubject();
    }

    private static Claims getAllClaimsFromToken(String token, String secret) {
        return Jwts.parser()
                .setSigningKey(secret.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }

    public static String getUserIdFromToken(String token, String secret) {
        return getAllClaimsFromToken(token, secret).get("userId").toString();
    }
}
