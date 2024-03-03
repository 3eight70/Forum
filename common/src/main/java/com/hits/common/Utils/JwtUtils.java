package com.hits.common.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
<<<<<<< HEAD
=======
import org.springframework.beans.factory.annotation.Value;
>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {
<<<<<<< HEAD
    public static Claims parseToken(String token, String secret){
        return Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token).getBody();
=======

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
>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d
    }
}
