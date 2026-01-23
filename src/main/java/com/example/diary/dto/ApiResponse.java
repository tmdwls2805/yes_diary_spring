package com.example.diary.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * API 공통 응답 형식
 */
@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private int status;      // HTTP 상태 코드 (200, 201, 400 등)
    private String message;  // 응답 메시지
    private T data;          // 실제 데이터 (없으면 null)
    
    /**
     * 필드 에러 정보
     * Validation 에러 등에서 사용
     */
    @Getter
    @AllArgsConstructor
    public static class FieldError {
        private String field;    // 에러가 발생한 필드명
        private String message;  // 에러 메시지
    }
}
