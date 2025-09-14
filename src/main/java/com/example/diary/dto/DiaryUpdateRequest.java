package com.example.diary.dto;

import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DiaryUpdateRequest {

    @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다")
    private String title;

    @Size(max = 1000, message = "내용은 1000자를 초과할 수 없습니다")
    private String content;

    @Builder
    public DiaryUpdateRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
