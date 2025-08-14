package com.next.app.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKeyRaw; // application.yml에서 주입

    @Value("${jwt.expiration-ms}")
    private long validityInMs;

    private SecretKey secretKey; // 실제 사용할 시크릿 키 객체

    @PostConstruct
    public void init() {
        if (secretKeyRaw == null || secretKeyRaw.length() < 32) {
            throw new IllegalStateException("JWT Secret must be at least 32 characters long");
        }
        this.secretKey = Keys.hmacShaKeyFor(secretKeyRaw.getBytes(StandardCharsets.UTF_8));
    }

    // 1. 토큰 생성
    public String generateToken(String email) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMs);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 2. 요청 헤더에서 토큰 추출 (Bearer 제거)
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.toLowerCase().startsWith("bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 3. 토큰 유효성 & 만료 확인
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            // 잘못되었거나 만료된 토큰
            return false;
        }
    }

    // 4. 토큰에서 이메일(주체) 추출
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}

