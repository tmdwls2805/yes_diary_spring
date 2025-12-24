package com.example.diary.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
public class DiaryListResponse {
    private Integer year;
    private Integer month;
    private Integer totalCount;
    private List<DiaryResponse> diaries;
}
