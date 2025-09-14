package com.example.diary.service;

import com.example.diary.dto.DiaryCreateRequest;
import com.example.diary.dto.DiaryResponse;
import com.example.diary.dto.DiaryUpdateRequest;
import com.example.diary.entity.Diary;
import com.example.diary.repository.DiaryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class DiaryServiceTest {

    @Autowired
    private DiaryService diaryService;

    @Autowired
    private DiaryRepository diaryRepository;

    @BeforeEach
    void setUp() {
        diaryRepository.deleteAll();
    }

    @Test
    @DisplayName("일기 생성 테스트")
    void createDiary() {
        // given
        DiaryCreateRequest request = DiaryCreateRequest.builder()
                .title("새로운 일기")
                .content("새로운 내용")
                .author("새로운 작성자")
                .build();

        // when
        DiaryResponse response = diaryService.createDiary(request);

        // then
        assertThat(response.getId()).isNotNull();
        assertThat(response.getTitle()).isEqualTo("새로운 일기");
        assertThat(response.getContent()).isEqualTo("새로운 내용");
        assertThat(response.getAuthor()).isEqualTo("새로운 작성자");
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("일기 조회 테스트")
    void getDiary() {
        // given
        Diary diary = Diary.builder()
                .title("조회할 일기")
                .content("조회할 내용")
                .author("조회할 작성자")
                .build();
        Diary savedDiary = diaryRepository.save(diary);

        // when
        DiaryResponse response = diaryService.getDiary(savedDiary.getId());

        // then
        assertThat(response.getId()).isEqualTo(savedDiary.getId());
        assertThat(response.getTitle()).isEqualTo("조회할 일기");
        assertThat(response.getContent()).isEqualTo("조회할 내용");
        assertThat(response.getAuthor()).isEqualTo("조회할 작성자");
    }

    @Test
    @DisplayName("존재하지 않는 일기 조회 테스트")
    void getDiaryNotFound() {
        // when & then
        assertThatThrownBy(() -> diaryService.getDiary(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("일기를 찾을 수 없습니다. ID: 999");
    }

    @Test
    @DisplayName("모든 일기 조회 테스트")
    void getAllDiaries() {
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

        // when
        List<DiaryResponse> responses = diaryService.getAllDiaries();

        // then
        assertThat(responses).hasSize(2);
    }

    @Test
    @DisplayName("일기 수정 테스트")
    void updateDiary() {
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

        // when
        DiaryResponse response = diaryService.updateDiary(savedDiary.getId(), request);

        // then
        assertThat(response.getTitle()).isEqualTo("수정된 제목");
        assertThat(response.getContent()).isEqualTo("수정된 내용");
        assertThat(response.getAuthor()).isEqualTo("원래 작성자");
    }

    @Test
    @DisplayName("일기 삭제 테스트")
    void deleteDiary() {
        // given
        Diary diary = Diary.builder()
                .title("삭제할 일기")
                .content("삭제할 내용")
                .author("삭제할 작성자")
                .build();
        Diary savedDiary = diaryRepository.save(diary);

        // when
        diaryService.deleteDiary(savedDiary.getId());

        // then
        Optional<Diary> deletedDiary = diaryRepository.findById(savedDiary.getId());
        assertThat(deletedDiary).isEmpty();
    }

    @Test
    @DisplayName("작성자별 일기 조회 테스트")
    void getDiariesByAuthor() {
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

        // when
        List<DiaryResponse> responses = diaryService.getDiariesByAuthor("홍길동");

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses).extracting("author").containsOnly("홍길동");
    }
}
