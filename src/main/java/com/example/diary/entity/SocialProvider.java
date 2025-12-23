package com.example.diary.entity;

/**
 * 소셜 로그인 제공자 타입
 */
public enum SocialProvider {
    APPLE("애플"),
    KAKAO("카카오");

    private final String description;

    SocialProvider(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
