package com.example.diary.dto;

import com.example.diary.entity.User;
import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private UserInfo user;

    @Getter
    public static class UserInfo {
        private Long id;
        private String nickname;
        private String provider;
        private String department;
        private String workStartTime;
        private String workEndTime;
        private String onboardingEmotion;

        public UserInfo(User user) {
            this.id = user.getId();
            this.nickname = user.getNickname();
            this.provider = user.getProvider().name();
            this.department = user.getDepartment();
            this.workStartTime = user.getWorkStartTime();
            this.workEndTime = user.getWorkEndTime();
            this.onboardingEmotion = user.getOnboardingEmotion();
        }
    }
}
