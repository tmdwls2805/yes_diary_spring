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

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
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
                .content(request.getContent())
                .cardMessage(request.getCardMessage())
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
        if (request.getContent() != null) {
            diary.updateContent(request.getContent());
        }
        if (request.getEmotionId() != null) {
            Emotion emotion = emotionRepository.findById(request.getEmotionId())
                    .orElseThrow(() -> new IllegalArgumentException("감정을 찾을 수 없습니다"));
            diary.updateEmotion(emotion);
        }
        if (request.getCardMessage() != null) {
            diary.updateCardMessage(request.getCardMessage());
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

    /**
     * 로컬 일기 동기화 (SSE 진행률 포함)
     * 실시간으로 동기화 진행률(%)을 전송
     *
     * 동기화 규칙 (충돌 해결 로직 포함):
     * 1. 로컬에 일기 있음 + 서버에 같은 날짜 일기 있음
     *    → updatedAt 비교하여 최신 데이터 우선
     *    - 로컬이 더 최신: 서버 업데이트
     *    - 서버가 더 최신 또는 동일: 서버 데이터 유지
     * 2. 로컬에 일기 있음 + 서버에 해당 날짜 일기 없음
     *    → 로컬 데이터를 서버에 새로 생성
     * 3. 로컬에 일기 없음 + 서버에 일기 있음
     *    → 서버 데이터 유지 (allDiaries에 포함되어 반환)
     */
    @Transactional
    public void syncDiariesWithProgress(String authHeader, DiarySyncRequest request, SseEmitter emitter) {
        try {
            // 1. JWT 토큰에서 userId 추출
            Long userId = jwtService.getUserIdFromAuthHeader(authHeader);

            // 2. 유저 조회
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));

            int total = request.getLocalDiaries().size();
            int createdCount = 0;
            int updatedCount = 0;
            int skippedCount = 0;  // 서버가 더 최신이어서 스킵한 개수

            // 시작 이벤트 전송
            sendProgress(emitter, SyncProgressEvent.progress(0, total, "동기화 시작"));

            // 3. 로컬 일기들을 순회하며 동기화
            for (int i = 0; i < request.getLocalDiaries().size(); i++) {
                DiarySyncRequest.LocalDiary localDiary = request.getLocalDiaries().get(i);

                // 감정 조회
                Emotion emotion = emotionRepository.findById(localDiary.getEmotionId())
                        .orElseThrow(() -> new IllegalArgumentException("감정을 찾을 수 없습니다: " + localDiary.getEmotionId()));

                // 서버에 같은 날짜의 일기가 있는지 확인
                Optional<Diary> existingDiary = diaryRepository.findByUserAndDate(user, localDiary.getDate());

                if (existingDiary.isPresent()) {
                    // 충돌 해결: updatedAt 비교
                    Diary serverDiary = existingDiary.get();
                    LocalDateTime localUpdatedAt = localDiary.getUpdatedAt();
                    LocalDateTime serverUpdatedAt = serverDiary.getUpdatedAt();

                    // updatedAt이 null인 경우 처리 (하위 호환성)
                    if (localUpdatedAt == null) {
                        // 로컬에 updatedAt이 없으면 date를 기준으로 처리
                        localUpdatedAt = localDiary.getDate().atStartOfDay();
                    }

                    // 로컬이 더 최신이면 서버 업데이트
                    if (localUpdatedAt.isAfter(serverUpdatedAt)) {
                        serverDiary.updateContent(localDiary.getContent());
                        serverDiary.updateEmotion(emotion);
                        serverDiary.updateCardMessage(localDiary.getCardMessage());
                        updatedCount++;
                        log.info("일기 업데이트 (로컬 우선): userId={}, date={}, localTime={}, serverTime={}",
                                userId, localDiary.getDate(), localUpdatedAt, serverUpdatedAt);
                    } else {
                        // 서버가 더 최신이거나 동일하면 서버 데이터 유지
                        skippedCount++;
                        log.info("일기 스킵 (서버 우선): userId={}, date={}, localTime={}, serverTime={}",
                                userId, localDiary.getDate(), localUpdatedAt, serverUpdatedAt);
                    }
                } else {
                    // 서버에 일기 없음 → 새로 생성
                    Diary newDiary = Diary.builder()
                            .content(localDiary.getContent())
                            .cardMessage(localDiary.getCardMessage())
                            .date(localDiary.getDate())
                            .user(user)
                            .emotion(emotion)
                            .build();
                    diaryRepository.save(newDiary);
                    createdCount++;
                    log.info("일기 생성: userId={}, date={}", userId, localDiary.getDate());
                }

                // 진행률 전송
                int current = i + 1;
                String message = String.format("%d/%d 처리 완료", current, total);
                sendProgress(emitter, SyncProgressEvent.progress(current, total, message));

                // 약간의 지연 (선택사항, 진행률을 더 잘 볼 수 있게)
                Thread.sleep(100);
            }

            // 4. 동기화 후 서버의 모든 일기 조회
            List<Diary> allDiaries = diaryRepository.findByUserOrderByDateDesc(user);
            List<DiaryResponse> diaryResponses = allDiaries.stream()
                    .map(DiaryResponse::from)
                    .collect(Collectors.toList());

            int syncedCount = createdCount + updatedCount;
            DiarySyncResponse result = new DiarySyncResponse(syncedCount, createdCount, updatedCount, diaryResponses);

            log.info("동기화 완료: userId={}, created={}, updated={}, skipped={}, total={}",
                    userId, createdCount, updatedCount, skippedCount, syncedCount);

            // 완료 이벤트 전송
            sendProgress(emitter, SyncProgressEvent.completed(result));
            emitter.complete();

        } catch (Exception e) {
            log.error("동기화 실패: {}", e.getMessage(), e);
            try {
                sendProgress(emitter, SyncProgressEvent.error(e.getMessage()));
                emitter.completeWithError(e);
            } catch (IOException ioException) {
                log.error("에러 전송 실패", ioException);
            }
        }
    }

    private void sendProgress(SseEmitter emitter, SyncProgressEvent event) throws IOException {
        emitter.send(SseEmitter.event()
                .name("sync-progress")
                .data(event));
    }
}
