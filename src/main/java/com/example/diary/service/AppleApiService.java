package com.example.diary.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AppleApiService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String APPLE_PUBLIC_KEYS_URL = "https://appleid.apple.com/auth/keys";
    private static final String APPLE_ISSUER = "https://appleid.apple.com";

    @Value("${apple.client-id:com.example.diary}")
    private String appleClientId;

    /**
     * Apple Identity Token 검증
     */
    public Map<String, Object> verifyIdentityToken(String identityToken) {
        try {
            // 1. JWT 헤더에서 kid 추출
            String kid = getKidFromToken(identityToken);

            // 2. Apple 공개키 가져오기
            PublicKey publicKey = getApplePublicKey(kid);

            // 3. JWT 검증 및 디코딩
            Claims claims = Jwts.parser()
                    .setSigningKey(publicKey)
                    .parseClaimsJws(identityToken)
                    .getBody();

            // 4. Issuer 검증
            String issuer = claims.getIssuer();
            if (!APPLE_ISSUER.equals(issuer)) {
                throw new RuntimeException("Invalid Apple token issuer: " + issuer);
            }

            // 5. Audience 검증
            String audience = claims.getAudience();
            if (!appleClientId.equals(audience)) {
                log.warn("Apple Client ID mismatch. Expected: {}, Got: {}", appleClientId, audience);
                // 개발 환경에서는 경고만 출력하고 진행
                // 운영 환경에서는 예외 발생시킬 것
                // throw new RuntimeException("Invalid Apple token audience");
            }

            // 6. 사용자 정보 추출
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("sub", claims.getSubject());  // Apple User ID
            userInfo.put("email", claims.get("email", String.class));
            userInfo.put("email_verified", claims.get("email_verified", String.class));

            log.info("Apple token verified successfully. sub={}", claims.getSubject());

            return userInfo;

        } catch (ExpiredJwtException e) {
            log.error("Apple token expired: {}", e.getMessage());
            throw new RuntimeException("Apple token has expired", e);
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException e) {
            log.error("Invalid Apple token: {}", e.getMessage());
            throw new RuntimeException("Invalid Apple token", e);
        } catch (Exception e) {
            log.error("Apple token verification failed: {}", e.getMessage(), e);
            throw new RuntimeException("Apple token verification failed", e);
        }
    }

    /**
     * JWT 헤더에서 kid 추출
     */
    private String getKidFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                throw new RuntimeException("Invalid JWT format");
            }

            String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
            JsonNode header = objectMapper.readTree(headerJson);
            String kid = header.get("kid").asText();

            if (kid == null || kid.isEmpty()) {
                throw new RuntimeException("kid not found in token header");
            }

            return kid;
        } catch (Exception e) {
            log.error("Failed to extract kid from token: {}", e.getMessage());
            throw new RuntimeException("Failed to extract kid from token", e);
        }
    }

    /**
     * Apple 공개키 가져오기
     */
    private PublicKey getApplePublicKey(String kid) {
        try {
            // Apple 공개키 엔드포인트 호출
            ResponseEntity<String> response = restTemplate.exchange(
                    APPLE_PUBLIC_KEYS_URL,
                    HttpMethod.GET,
                    null,
                    String.class
            );

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Failed to fetch Apple public keys");
            }

            JsonNode keysNode = objectMapper.readTree(response.getBody());
            JsonNode keys = keysNode.get("keys");

            // kid에 해당하는 키 찾기
            for (JsonNode key : keys) {
                if (kid.equals(key.get("kid").asText())) {
                    return generatePublicKey(key);
                }
            }

            throw new RuntimeException("Public key with kid " + kid + " not found");

        } catch (Exception e) {
            log.error("Failed to get Apple public key: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get Apple public key", e);
        }
    }

    /**
     * JWK에서 PublicKey 생성
     */
    private PublicKey generatePublicKey(JsonNode key) {
        try {
            String n = key.get("n").asText();
            String e = key.get("e").asText();

            byte[] nBytes = Base64.getUrlDecoder().decode(n);
            byte[] eBytes = Base64.getUrlDecoder().decode(e);

            BigInteger modulus = new BigInteger(1, nBytes);
            BigInteger exponent = new BigInteger(1, eBytes);

            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulus, exponent);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            return keyFactory.generatePublic(publicKeySpec);

        } catch (Exception e) {
            log.error("Failed to generate public key: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate public key", e);
        }
    }

    /**
     * Apple User ID 추출
     */
    public String extractAppleId(Map<String, Object> appleUserInfo) {
        Object sub = appleUserInfo.get("sub");
        return sub != null ? sub.toString() : null;
    }

    /**
     * Email 추출
     */
    public String extractEmail(Map<String, Object> appleUserInfo) {
        Object email = appleUserInfo.get("email");
        return email != null ? email.toString() : "";
    }
}
