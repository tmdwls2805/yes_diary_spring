package com.example.diary.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class DiaryCreateRequest {
    private String content;
    private Long emotionId;
    private String cardMessage;
    private LocalDate date;
}
