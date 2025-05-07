package com.books.api.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.util.*;
import java.util.Date;
import javax.crypto.SecretKey;

public class JwtUtil {

    // Chave secreta (pode vir do banco ou config)
    private static final String SECRET = "super-secret-key-for-jwt-generation-1234567890";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    // Tempo de vida: 2 horas (em ms)
    private static final long EXPIRATION = 2 * 60 * 60 * 1000;

    public static String generateToken(String subject, Map<String, Object> claims) {
        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Jws<Claims> parseToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token);
    }
}
