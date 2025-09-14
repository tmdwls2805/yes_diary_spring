package com.example.diary.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

class DiaryTest {

    @Test
    @DisplayName("일기 엔티티 생성 테스트")
    void createDiary() {
        // given
        String title = "오늘의 일기";
        String content = "오늘은 좋은 하루였습니다.";
        String author = "홍길동";

        // when
        Diary diary = Diary.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();

        // then
        assertThat(diary.getTitle()).isEqualTo(title);
        assertThat(diary.getContent()).isEqualTo(content);
        assertThat(diary.getAuthor()).isEqualTo(author);
        assertThat(diary.getCreatedAt()).isNotNull();
        assertThat(diary.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("일기 내용 수정 테스트")
    void updateDiary() {
        // given
        Diary diary = Diary.builder()
                .title("제목")
                .content("원래 내용")
                .author("작성자")
                .build();

        LocalDateTime originalUpdatedAt = diary.getUpdatedAt();

        // when
        diary.updateContent("수정된 내용");

        // then
        assertThat(diary.getContent()).isEqualTo("수정된 내용");
        assertThat(diary.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    @DisplayName("일기 제목 수정 테스트")
    void updateTitle() {
        // given
        Diary diary = Diary.builder()
                .title("원래 제목")
                .content("내용")
                .author("작성자")
                .build();

        // when
        diary.updateTitle("수정된 제목");

        // then
        assertThat(diary.getTitle()).isEqualTo("수정된 제목");
    }
}
