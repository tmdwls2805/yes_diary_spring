package com.example.diary.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class AppleLoginCheckResponse {
    private boolean isExistingUser;
    private AppleUserInfo appleInfo;
    private TokenInfo tokens;

    @Getter
    @AllArgsConstructor
    public static class AppleUserInfo {
        private String appleId;  // Apple User ID (sub)
        private String email;    // Email (optional)
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
    }
}
