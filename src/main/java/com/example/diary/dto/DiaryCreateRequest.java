package com.example.diary.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DiaryCreateRequest {

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다")
    private String title;

    @NotBlank(message = "내용은 필수입니다")
    private String content;

    @NotBlank(message = "작성자는 필수입니다")
    @Size(max = 50, message = "작성자명은 50자를 초과할 수 없습니다")
    private String author;

    @Builder
    public DiaryCreateRequest(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }
}
