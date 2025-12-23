package com.example.diary.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret:my-secret-key-for-jwt-token-generation-minimum-512-bits}")
    private String secretKey;

    @Value("${jwt.access-token-validity:3600000}")  // 기본 1시간 (밀리초)
    private long accessTokenValidity;

    @Value("${jwt.refresh-token-validity:1209600000}")  // 기본 14일 (밀리초)
    private long refreshTokenValidity;

    public String generateAccessToken(Long userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    public String generateRefreshToken(Long userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    /**
     * JWT 토큰에서 userId 추출
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    /**
     * Authorization 헤더에서 Bearer 토큰 추출 후 userId 반환
     */
    public Long getUserIdFromAuthHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }

        String token = authHeader.substring(7);  // "Bearer " 제거
        return getUserIdFromToken(token);
    }
}
