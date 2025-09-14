package com.example.diary.controller;

import com.example.diary.dto.DiaryCreateRequest;
import com.example.diary.dto.DiaryResponse;
import com.example.diary.dto.DiaryUpdateRequest;
import com.example.diary.service.DiaryService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping
    public ResponseEntity<DiaryResponse> createDiary(@Valid @RequestBody DiaryCreateRequest request) {
        DiaryResponse response = diaryService.createDiary(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiaryResponse> getDiary(@PathVariable Long id) {
        try {
            DiaryResponse response = diaryService.getDiary(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<DiaryResponse>> getAllDiaries() {
        List<DiaryResponse> responses = diaryService.getAllDiaries();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiaryResponse> updateDiary(
            @PathVariable Long id,
            @Valid @RequestBody DiaryUpdateRequest request) {
        try {
            DiaryResponse response = diaryService.updateDiary(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiary(@PathVariable Long id) {
        try {
            diaryService.deleteDiary(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/author/{author}")
    public ResponseEntity<List<DiaryResponse>> getDiariesByAuthor(@PathVariable String author) {
        List<DiaryResponse> responses = diaryService.getDiariesByAuthor(author);
        return ResponseEntity.ok(responses);
    }
}
