package com.example.diary.config;

import com.example.diary.entity.Emotion;
import com.example.diary.repository.EmotionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 애플리케이션 시작 시 초기 데이터 삽입
 * - Emotion 테이블에 5가지 감정 데이터 자동 삽입
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final EmotionRepository emotionRepository;

    @Override
    public void run(String... args) throws Exception {
        // Emotion 데이터가 없으면 초기 데이터 삽입
        if (emotionRepository.count() == 0) {
            log.info("Emotion 초기 데이터 삽입 시작...");

            emotionRepository.save(Emotion.builder()
                    .name("red")
                    .imageUrl("assets/emotion/red.svg")
                    .build());

            emotionRepository.save(Emotion.builder()
                    .name("yellow")
                    .imageUrl("assets/emotion/yellow.svg")
                    .build());

            emotionRepository.save(Emotion.builder()
                    .name("blue")
                    .imageUrl("assets/emotion/blue.svg")
                    .build());

            emotionRepository.save(Emotion.builder()
                    .name("pink")
                    .imageUrl("assets/emotion/pink.svg")
                    .build());

            emotionRepository.save(Emotion.builder()
                    .name("green")
                    .imageUrl("assets/emotion/green.svg")
                    .build());

            log.info("Emotion 초기 데이터 삽입 완료! (5개)");
        } else {
            log.info("Emotion 데이터가 이미 존재합니다. ({}개)", emotionRepository.count());
        }
    }
}
