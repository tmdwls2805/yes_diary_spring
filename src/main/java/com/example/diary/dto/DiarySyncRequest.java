package com.example.diary.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class DiarySyncRequest {
    private List<LocalDiary> localDiaries;

    @Getter
    @NoArgsConstructor
    public static class LocalDiary {
        private Long localId;        // 로컬 DB의 ID (서버는 무시)
        private String title;
        private String content;
        private Long emotionId;
        private LocalDate date;
    }
}
