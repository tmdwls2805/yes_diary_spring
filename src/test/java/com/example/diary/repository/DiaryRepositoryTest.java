package com.example.diary.repository;

import com.example.diary.entity.Diary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class DiaryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DiaryRepository diaryRepository;

    @Test
    @DisplayName("일기 저장 테스트")
    void saveDiary() {
        // given
        Diary diary = Diary.builder()
                .title("테스트 일기")
                .content("테스트 내용")
                .author("테스트 작성자")
                .build();

        // when
        Diary savedDiary = diaryRepository.save(diary);

        // then
        assertThat(savedDiary.getId()).isNotNull();
        assertThat(savedDiary.getTitle()).isEqualTo("테스트 일기");
        assertThat(savedDiary.getContent()).isEqualTo("테스트 내용");
        assertThat(savedDiary.getAuthor()).isEqualTo("테스트 작성자");
    }

    @Test
    @DisplayName("작성자별 일기 조회 테스트")
    void findByAuthor() {
        // given
        Diary diary1 = Diary.builder()
                .title("일기1")
                .content("내용1")
                .author("홍길동")
                .build();

        Diary diary2 = Diary.builder()
                .title("일기2")
                .content("내용2")
                .author("홍길동")
                .build();

        Diary diary3 = Diary.builder()
                .title("일기3")
                .content("내용3")
                .author("김철수")
                .build();

        entityManager.persistAndFlush(diary1);
        entityManager.persistAndFlush(diary2);
        entityManager.persistAndFlush(diary3);

        // when
        List<Diary> hongDiaries = diaryRepository.findByAuthor("홍길동");

        // then
        assertThat(hongDiaries).hasSize(2);
        assertThat(hongDiaries).extracting("author").containsOnly("홍길동");
    }

    @Test
    @DisplayName("ID로 일기 조회 테스트")
    void findById() {
        // given
        Diary diary = Diary.builder()
                .title("조회 테스트")
                .content("조회 내용")
                .author("조회 작성자")
                .build();

        Diary savedDiary = entityManager.persistAndFlush(diary);

        // when
        Optional<Diary> foundDiary = diaryRepository.findById(savedDiary.getId());

        // then
        assertThat(foundDiary).isPresent();
        assertThat(foundDiary.get().getTitle()).isEqualTo("조회 테스트");
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회 테스트")
    void findByIdNotFound() {
        // when
        Optional<Diary> foundDiary = diaryRepository.findById(999L);

        // then
        assertThat(foundDiary).isEmpty();
    }

    @Test
    @DisplayName("모든 일기 조회 테스트")
    void findAll() {
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

        entityManager.persistAndFlush(diary1);
        entityManager.persistAndFlush(diary2);

        // when
        List<Diary> allDiaries = diaryRepository.findAll();

        // then
        assertThat(allDiaries).hasSize(2);
    }
}
