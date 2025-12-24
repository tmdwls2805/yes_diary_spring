package com.example.diary.controller;

import com.example.diary.dto.*;
import com.example.diary.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    /**
     * 일기 생성
     * POST /api/diaries
     * Header: Authorization: Bearer {accessToken}
     */
    @PostMapping
    public ResponseEntity<DiaryResponse> createDiary(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody DiaryCreateRequest request) {
        DiaryResponse response = diaryService.createDiary(authHeader, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 일기 단건 조회
     * GET /api/diaries/{id}
     * Header: Authorization: Bearer {accessToken}
     */
    @GetMapping("/{id}")
    public ResponseEntity<DiaryResponse> getDiary(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        DiaryResponse response = diaryService.getDiary(authHeader, id);
        return ResponseEntity.ok(response);
    }

    /**
     * 월별 일기 조회
     * GET /api/diaries/monthly?year=2025&month=12
     * Header: Authorization: Bearer {accessToken}
     */
    @GetMapping("/monthly")
    public ResponseEntity<DiaryListResponse> getMonthlyDiaries(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        DiaryListResponse response = diaryService.getMonthlyDiaries(authHeader, year, month);
        return ResponseEntity.ok(response);
    }

    /**
     * 일기 수정
     * PUT /api/diaries/{id}
     * Header: Authorization: Bearer {accessToken}
     */
    @PutMapping("/{id}")
    public ResponseEntity<DiaryResponse> updateDiary(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @RequestBody DiaryUpdateRequest request) {
        DiaryResponse response = diaryService.updateDiary(authHeader, id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 일기 삭제
     * DELETE /api/diaries/{id}
     * Header: Authorization: Bearer {accessToken}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiary(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        diaryService.deleteDiary(authHeader, id);
        return ResponseEntity.ok().build();
    }

    /**
     * 로컬 일기 동기화
     * POST /api/diaries/sync
     * Header: Authorization: Bearer {accessToken}
     *
     * 로그인 시 로컬에 저장된 일기들을 서버와 동기화
     * - 로컬에 일기 있음 + 서버에 같은 날짜 일기 있음 → 로컬 데이터로 서버 덮어쓰기
     * - 로컬에 일기 있음 + 서버에 해당 날짜 일기 없음 → 로컬 데이터를 서버에 새로 생성
     * - 동기화 후 서버의 모든 일기를 반환 (클라이언트는 이를 로컬 DB에 저장)
     */
    @PostMapping("/sync")
    public ResponseEntity<DiarySyncResponse> syncDiaries(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody DiarySyncRequest request) {
        DiarySyncResponse response = diaryService.syncDiaries(authHeader, request);
        return ResponseEntity.ok(response);
    }
}
