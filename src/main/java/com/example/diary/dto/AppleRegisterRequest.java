package com.example.diary.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AppleRegisterRequest {
    private String identityToken;  // Apple ID Token
    private String nickname;
}
