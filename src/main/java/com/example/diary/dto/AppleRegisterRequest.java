package com.example.diary.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AppleRegisterRequest {
    private String identityToken;  // Apple ID Token
    private String nickname;
    private String password;  // PIN 번호 (4자리 숫자, optional)
    private String department;
    private String workStartTime;
    private String workEndTime;
    private String onboardingEmotion;
}
