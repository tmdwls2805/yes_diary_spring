package com.example.diary.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(
    name = "diary",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_user_date",
            columnNames = {"user_id", "date"}
        )
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diary extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;  // LocalDateTime이 아닌 LocalDate 사용 (날짜만)

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emotion_id", nullable = false)
    private Emotion emotion;

    @Builder
    public Diary(LocalDate date, String title, String content, User user, Emotion emotion) {
        super();
        this.date = date;
        this.title = title;
        this.content = content;
        this.user = user;
        this.emotion = emotion;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateEmotion(Emotion emotion) {
        this.emotion = emotion;
    }
}
