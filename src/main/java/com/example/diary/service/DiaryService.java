package com.example.diary.service;

import com.example.diary.dto.DiaryCreateRequest;
import com.example.diary.dto.DiaryResponse;
import com.example.diary.dto.DiaryUpdateRequest;
import com.example.diary.entity.Diary;
import com.example.diary.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryService {

    private final DiaryRepository diaryRepository;

    @Transactional
    public DiaryResponse createDiary(DiaryCreateRequest request) {
        Diary diary = Diary.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(request.getAuthor())
                .build();

        Diary savedDiary = diaryRepository.save(diary);
        return DiaryResponse.from(savedDiary);
    }

    public DiaryResponse getDiary(Long id) {
        Diary diary = diaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("일기를 찾을 수 없습니다. ID: " + id));
        return DiaryResponse.from(diary);
    }

    public List<DiaryResponse> getAllDiaries() {
        return diaryRepository.findAll()
                .stream()
                .map(DiaryResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public DiaryResponse updateDiary(Long id, DiaryUpdateRequest request) {
        Diary diary = diaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("일기를 찾을 수 없습니다. ID: " + id));

        if (request.getTitle() != null) {
            diary.updateTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            diary.updateContent(request.getContent());
        }

        return DiaryResponse.from(diary);
    }

    @Transactional
    public void deleteDiary(Long id) {
        if (!diaryRepository.existsById(id)) {
            throw new RuntimeException("일기를 찾을 수 없습니다. ID: " + id);
        }
        diaryRepository.deleteById(id);
    }

    public List<DiaryResponse> getDiariesByAuthor(String author) {
        return diaryRepository.findByAuthor(author)
                .stream()
                .map(DiaryResponse::from)
                .collect(Collectors.toList());
    }
}
