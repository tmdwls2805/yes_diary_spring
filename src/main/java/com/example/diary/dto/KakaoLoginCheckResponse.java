package com.example.diary.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class KakaoLoginCheckResponse {
    private boolean isExistingUser;
    private KakaoUserInfo kakaoInfo;
    private TokenInfo tokens;

    @Getter
    @AllArgsConstructor
    public static class KakaoUserInfo {
        private String socialId;  // 카카오에서 받은 소셜 ID만 포함 (닉네임은 사용자가 직접 입력)
    }

    @Getter
    @AllArgsConstructor
    public static class TokenInfo {
        private String accessToken;
        private String refreshToken;
        private UserInfo user;
    }

    @Getter
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String nickname;
        private String provider;
        private String createdAt;
        private String updatedAt;
        private String department;
        private String workStartTime;
        private String workEndTime;
        private String onboardingEmotion;
    }
}
