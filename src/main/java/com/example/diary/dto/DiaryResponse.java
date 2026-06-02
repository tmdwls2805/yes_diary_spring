package com.example.diary.dto;

import com.example.diary.entity.Diary;
import lombok.Getter;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DiaryResponse {
    private Long id;
    private String content;
    private String cardMessage;
    private EmotionInfo emotionInfo;
    private LocalDate date;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @AllArgsConstructor
    public static class EmotionInfo {
        private Long id;
        private String name;
        private String imageUrl;
    }

    public static DiaryResponse from(Diary diary) {
        return new DiaryResponse(
            diary.getId(),
            diary.getContent(),
            diary.getCardMessage(),
            new EmotionInfo(
                diary.getEmotion().getId(),
                diary.getEmotion().getName(),
                diary.getEmotion().getImageUrl()
            ),
            diary.getDate(),
            diary.getUser().getId(),
            diary.getCreatedAt(),
            diary.getUpdatedAt()
        );
    }
}
