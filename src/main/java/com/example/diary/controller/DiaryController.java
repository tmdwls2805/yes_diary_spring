package com.example.diary.controller;

import com.example.diary.dto.*;
import com.example.diary.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * 일기 생성
     * POST /api/diaries
     * Header: Authorization: Bearer {accessToken}
     */
    @PostMapping
    public ResponseEntity<ApiResponse<DiaryResponse>> createDiary(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody DiaryCreateRequest request) {
        DiaryResponse response = diaryService.createDiary(authHeader, request);
        return ResponseEntity.ok(new ApiResponse<>(200, "일기 생성 완료", response));
    }

    /**
     * 일기 단건 조회
     * GET /api/diaries/{id}
     * Header: Authorization: Bearer {accessToken}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DiaryResponse>> getDiary(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        DiaryResponse response = diaryService.getDiary(authHeader, id);
        return ResponseEntity.ok(new ApiResponse<>(200, "일기 조회 완료", response));
    }

    /**
     * 월별 일기 조회
     * GET /api/diaries/monthly?year=2025&month=12
     * Header: Authorization: Bearer {accessToken}
     */
    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<DiaryListResponse>> getMonthlyDiaries(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        DiaryListResponse response = diaryService.getMonthlyDiaries(authHeader, year, month);
        return ResponseEntity.ok(new ApiResponse<>(200, "월별 일기 조회 완료", response));
    }

    /**
     * 일기 수정
     * PUT /api/diaries/{id}
     * Header: Authorization: Bearer {accessToken}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DiaryResponse>> updateDiary(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @RequestBody DiaryUpdateRequest request) {
        DiaryResponse response = diaryService.updateDiary(authHeader, id, request);
        return ResponseEntity.ok(new ApiResponse<>(200, "일기 수정 완료", response));
    }

    /**
     * 일기 삭제
     * DELETE /api/diaries/{id}
     * Header: Authorization: Bearer {accessToken}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteDiary(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        diaryService.deleteDiary(authHeader, id);
        return ResponseEntity.ok(new ApiResponse<>(200, "일기 삭제 완료", null));
    }

    /**
     * 로컬 일기 동기화 (SSE 진행률 포함)
     * POST /api/diaries/sync
     * Header: Authorization: Bearer {accessToken}
     * Content-Type: application/json
     *
     * 로그인 시 로컬에 저장된 일기들을 서버와 동기화하며 실시간 진행률을 SSE로 전송
     *
     * 동기화 규칙:
     * - 로컬에 일기 있음 + 서버에 같은 날짜 일기 있음 → 로컬 데이터로 서버 덮어쓰기
     * - 로컬에 일기 있음 + 서버에 해당 날짜 일기 없음 → 로컬 데이터를 서버에 새로 생성
     * - 동기화 후 서버의 모든 일기를 반환 (클라이언트는 이를 로컬 DB에 저장)
     *
     * SSE 이벤트 형식:
     * - event: sync-progress
     * - data: { "status": "progress", "percentage": 50, "current": 5, "total": 10, "message": "5/10 처리 완료" }
     * - 완료 시: { "status": "completed", "percentage": 100, "result": {...} }
     */
    @PostMapping(value = "/sync", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter syncDiaries(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody DiarySyncRequest request) {

        SseEmitter emitter = new SseEmitter(300000L); // 5분 타임아웃

        // 비동기로 동기화 실행
        executorService.execute(() -> {
            diaryService.syncDiariesWithProgress(authHeader, request, emitter);
        });

        return emitter;
    }
}
