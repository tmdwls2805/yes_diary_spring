package com.example.diary.controller;

import com.example.diary.dto.DiaryCreateRequest;
import com.example.diary.dto.DiaryResponse;
import com.example.diary.dto.DiaryUpdateRequest;
import com.example.diary.entity.Diary;
import com.example.diary.repository.DiaryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestMvc
@Transactional
@ActiveProfiles("test")
class DiaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DiaryRepository diaryRepository;

    @BeforeEach
    void setUp() {
        diaryRepository.deleteAll();
    }

    @Test
    @DisplayName("일기 생성 API 테스트")
    void createDiary() throws Exception {
        // given
        DiaryCreateRequest request = DiaryCreateRequest.builder()
                .title("새로운 일기")
                .content("새로운 내용")
                .author("새로운 작성자")
                .build();

        // when & then
        mockMvc.perform(post("/api/diaries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("새로운 일기"))
                .andExpect(jsonPath("$.content").value("새로운 내용"))
                .andExpect(jsonPath("$.author").value("새로운 작성자"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("일기 조회 API 테스트")
    void getDiary() throws Exception {
        // given
        Diary diary = Diary.builder()
                .title("조회할 일기")
                .content("조회할 내용")
                .author("조회할 작성자")
                .build();
        Diary savedDiary = diaryRepository.save(diary);

        // when & then
        mockMvc.perform(get("/api/diaries/{id}", savedDiary.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedDiary.getId()))
                .andExpect(jsonPath("$.title").value("조회할 일기"))
                .andExpect(jsonPath("$.content").value("조회할 내용"))
                .andExpect(jsonPath("$.author").value("조회할 작성자"));
    }

    @Test
    @DisplayName("존재하지 않는 일기 조회 API 테스트")
    void getDiaryNotFound() throws Exception {
        // when & then
        mockMvc.perform(get("/api/diaries/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("모든 일기 조회 API 테스트")
    void getAllDiaries() throws Exception {
        // given
        Diary diary1 = Diary.builder()
                .title("일기1")
                .content("내용1")
                .author("작성자1")
                .build();

        Diary diary2 = Diary.builder()
                .title("일기2")
                .content("내용2")
                .author("작성자2")
                .build();

        diaryRepository.save(diary1);
        diaryRepository.save(diary2);

        // when & then
        mockMvc.perform(get("/api/diaries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("일기 수정 API 테스트")
    void updateDiary() throws Exception {
        // given
        Diary diary = Diary.builder()
                .title("원래 제목")
                .content("원래 내용")
                .author("원래 작성자")
                .build();
        Diary savedDiary = diaryRepository.save(diary);

        DiaryUpdateRequest request = DiaryUpdateRequest.builder()
                .title("수정된 제목")
                .content("수정된 내용")
                .build();

        // when & then
        mockMvc.perform(put("/api/diaries/{id}", savedDiary.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정된 제목"))
                .andExpect(jsonPath("$.content").value("수정된 내용"))
                .andExpect(jsonPath("$.author").value("원래 작성자"));
    }

    @Test
    @DisplayName("일기 삭제 API 테스트")
    void deleteDiary() throws Exception {
        // given
        Diary diary = Diary.builder()
                .title("삭제할 일기")
                .content("삭제할 내용")
                .author("삭제할 작성자")
                .build();
        Diary savedDiary = diaryRepository.save(diary);

        // when & then
        mockMvc.perform(delete("/api/diaries/{id}", savedDiary.getId()))
                .andExpect(status().isNoContent());

        // 삭제 확인
        mockMvc.perform(get("/api/diaries/{id}", savedDiary.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("작성자별 일기 조회 API 테스트")
    void getDiariesByAuthor() throws Exception {
        // given
        Diary diary1 = Diary.builder()
                .title("홍길동의 일기1")
                .content("내용1")
                .author("홍길동")
                .build();

        Diary diary2 = Diary.builder()
                .title("홍길동의 일기2")
                .content("내용2")
                .author("홍길동")
                .build();

        Diary diary3 = Diary.builder()
                .title("김철수의 일기")
                .content("내용3")
                .author("김철수")
                .build();

        diaryRepository.save(diary1);
        diaryRepository.save(diary2);
        diaryRepository.save(diary3);

        // when & then
        mockMvc.perform(get("/api/diaries/author/{author}", "홍길동"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].author").value("홍길동"))
                .andExpect(jsonPath("$[1].author").value("홍길동"));
    }

    @Test
    @DisplayName("일기 생성 시 유효성 검증 테스트")
    void createDiaryValidation() throws Exception {
        // given
        DiaryCreateRequest request = DiaryCreateRequest.builder()
                .title("") // 빈 제목
                .content("내용")
                .author("작성자")
                .build();

        // when & then
        mockMvc.perform(post("/api/diaries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
