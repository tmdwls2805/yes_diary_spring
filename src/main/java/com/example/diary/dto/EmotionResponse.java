package com.example.diary.dto;

import com.example.diary.entity.Emotion;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmotionResponse {
    private Long id;
    private String name;
    private String imageUrl;

    public static EmotionResponse from(Emotion emotion) {
        return new EmotionResponse(
            emotion.getId(),
            emotion.getName(),
            emotion.getImageUrl()
        );
    }
}
