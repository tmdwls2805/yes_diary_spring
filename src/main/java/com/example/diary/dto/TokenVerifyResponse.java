package com.example.diary.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class TokenVerifyResponse {
    private boolean isValid;
    private Long id;
    private Integer remainingSeconds;
}
