package com.example.diary.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Slf4j
public class KakaoApiService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

    /**
     * 카카오 access_token으로 사용자 정보 조회
     */
    public Map<String, Object> getKakaoUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                KAKAO_USER_INFO_URL,
                HttpMethod.GET,
                entity,
                Map.class
            );

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("카카오 API 호출 실패");
            }

            return response.getBody();
        } catch (Exception e) {
            log.error("카카오 API 호출 실패: {}", e.getMessage());
            throw new RuntimeException("카카오 인증 실패", e);
        }
    }

    /**
     * 카카오 응답에서 socialId 추출
     */
    public String extractSocialId(Map<String, Object> kakaoUserInfo) {
        Object id = kakaoUserInfo.get("id");
        return id != null ? id.toString() : null;
    }
}