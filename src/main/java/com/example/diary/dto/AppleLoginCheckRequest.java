package com.example.diary.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AppleLoginCheckRequest {
    private String identityToken;  // Apple ID Token
}
