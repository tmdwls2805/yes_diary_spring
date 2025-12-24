package com.example.diary.service;

import com.example.diary.dto.*;
import com.example.diary.entity.Diary;
import com.example.diary.entity.Emotion;
import com.example.diary.entity.User;
import com.example.diary.repository.DiaryRepository;
import com.example.diary.repository.EmotionRepository;
import com.example.diary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;
    private final EmotionRepository emotionRepository;
    private final JwtService jwtService;

    /**
     * 일기 생성
     */
    @Transactional
    public DiaryResponse createDiary(String authHeader, DiaryCreateRequest request) {
        // 1. JWT 토큰에서 userId 추출
        Long userId = jwtService.getUserIdFromAuthHeader(authHeader);

        // 2. 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));

        // 3. 감정 조회
        Emotion emotion = emotionRepository.findById(request.getEmotionId())
                .orElseThrow(() -> new IllegalArgumentException("감정을 찾을 수 없습니다"));

        // 4. 중복 체크 (같은 날짜에 이미 일기가 있는지)
        if (diaryRepository.findByUserAndDate(user, request.getDate()).isPresent()) {
            throw new IllegalArgumentException("해당 날짜에 이미 일기가 존재합니다");
        }

        // 5. 일기 생성
        Diary diary = Diary.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .date(request.getDate())
                .user(user)
                .emotion(emotion)
                .build();

        Diary savedDiary = diaryRepository.save(diary);

        log.info("일기 생성 완료: userId={}, diaryId={}, date={}", userId, savedDiary.getId(), savedDiary.getDate());

        // 6. 응답 생성
        return DiaryResponse.from(savedDiary);
    }

    /**
     * 일기 단건 조회
     */
    public DiaryResponse getDiary(String authHeader, Long diaryId) {
        // 1. JWT 토큰에서 userId 추출
        Long userId = jwtService.getUserIdFromAuthHeader(authHeader);

        // 2. 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));

        // 3. 일기 조회
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("일기를 찾을 수 없습니다"));

        // 4. 권한 확인 (본인의 일기인지)
        if (!diary.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다");
        }

        return DiaryResponse.from(diary);
    }

    /**
     * 월별 일기 조회
     */
    public DiaryListResponse getMonthlyDiaries(String authHeader, Integer year, Integer month) {
        // 1. JWT 토큰에서 userId 추출
        Long userId = jwtService.getUserIdFromAuthHeader(authHeader);

        // 2. 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));

        // 3. 해당 월의 시작일과 종료일 계산
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // 4. 일기 조회
        List<Diary> diaries = diaryRepository.findByUserAndDateBetweenOrderByDateDesc(user, startDate, endDate);

        // 5. 응답 생성
        List<DiaryResponse> diaryResponses = diaries.stream()
                .map(DiaryResponse::from)
                .collect(Collectors.toList());

        log.info("월별 일기 조회: userId={}, year={}, month={}, count={}", userId, year, month, diaryResponses.size());

        return new DiaryListResponse(year, month, diaryResponses.size(), diaryResponses);
    }

    /**
     * 일기 수정
     */
    @Transactional
    public DiaryResponse updateDiary(String authHeader, Long diaryId, DiaryUpdateRequest request) {
        // 1. JWT 토큰에서 userId 추출
        Long userId = jwtService.getUserIdFromAuthHeader(authHeader);

        // 2. 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));

        // 3. 일기 조회
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("일기를 찾을 수 없습니다"));

        // 4. 권한 확인
        if (!diary.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다");
        }

        // 5. 일기 수정
        if (request.getTitle() != null) {
            diary.updateTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            diary.updateContent(request.getContent());
        }
        if (request.getEmotionId() != null) {
            Emotion emotion = emotionRepository.findById(request.getEmotionId())
                    .orElseThrow(() -> new IllegalArgumentException("감정을 찾을 수 없습니다"));
            diary.updateEmotion(emotion);
        }

        log.info("일기 수정 완료: userId={}, diaryId={}", userId, diaryId);

        return DiaryResponse.from(diary);
    }

    /**
     * 일기 삭제
     */
    @Transactional
    public void deleteDiary(String authHeader, Long diaryId) {
        // 1. JWT 토큰에서 userId 추출
        Long userId = jwtService.getUserIdFromAuthHeader(authHeader);

        // 2. 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));

        // 3. 일기 조회
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("일기를 찾을 수 없습니다"));

        // 4. 권한 확인
        if (!diary.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다");
        }

        // 5. 삭제
        diaryRepository.delete(diary);

        log.info("일기 삭제 완료: userId={}, diaryId={}", userId, diaryId);
    }
}
