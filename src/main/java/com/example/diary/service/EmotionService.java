package com.example.diary.service;

import com.example.diary.dto.EmotionResponse;
import com.example.diary.entity.Emotion;
import com.example.diary.repository.EmotionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EmotionService {

    private final EmotionRepository emotionRepository;

    /**
     * 모든 감정 조회
     */
    public List<EmotionResponse> getAllEmotions() {
        List<Emotion> emotions = emotionRepository.findAll();
        log.info("감정 조회 완료: {}개", emotions.size());

        return emotions.stream()
                .map(EmotionResponse::from)
                .collect(Collectors.toList());
    }
}
