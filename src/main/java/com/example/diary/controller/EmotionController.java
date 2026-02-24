package com.example.diary.controller;

import com.example.diary.dto.ApiResponse;
import com.example.diary.dto.EmotionResponse;
import com.example.diary.service.EmotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 감정 조회 API
 */
@RestController
@RequestMapping("/api/emotions")
@RequiredArgsConstructor
public class EmotionController {

    private final EmotionService emotionService;

    /**
     * 모든 감정 조회
     * GET /api/emotions
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<EmotionResponse>>> getAllEmotions() {
        List<EmotionResponse> emotions = emotionService.getAllEmotions();
        return ResponseEntity.ok(new ApiResponse<>(200, "감정 목록 조회 완료", emotions));
    }
}
