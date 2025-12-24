package com.example.diary.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
public class DiarySyncResponse {
    private Integer syncedCount;        // 동기화된 일기 개수 (생성 + 업데이트)
    private Integer createdCount;       // 새로 생성된 일기 개수
    private Integer updatedCount;       // 업데이트된 일기 개수
    private List<DiaryResponse> allDiaries;  // 동기화 후 서버의 모든 일기
}
