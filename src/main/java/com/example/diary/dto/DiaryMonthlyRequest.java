package com.example.diary.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DiaryMonthlyRequest {
    private Integer year;   // 예: 2025
    private Integer month;  // 1-12
}
