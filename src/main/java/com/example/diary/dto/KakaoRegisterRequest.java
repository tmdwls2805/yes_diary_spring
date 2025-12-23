package com.example.diary.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoRegisterRequest {
    private String accessToken;
    private String nickname;
}
