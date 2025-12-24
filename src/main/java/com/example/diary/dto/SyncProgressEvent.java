package com.example.diary.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class SyncProgressEvent {
    private String status;           // "progress", "completed", "error"
    private Integer percentage;       // 0-100
    private Integer current;          // 현재 처리한 일기 개수
    private Integer total;            // 총 일기 개수
    private String message;           // 상태 메시지
    private DiarySyncResponse result; // 완료 시에만 포함

    // 진행 중 이벤트 생성
    public static SyncProgressEvent progress(int current, int total, String message) {
        int percentage = total > 0 ? (current * 100 / total) : 0;
        return new SyncProgressEvent("progress", percentage, current, total, message, null);
    }

    // 완료 이벤트 생성
    public static SyncProgressEvent completed(DiarySyncResponse result) {
        return new SyncProgressEvent("completed", 100, result.getSyncedCount(),
                result.getSyncedCount(), "동기화 완료", result);
    }

    // 에러 이벤트 생성
    public static SyncProgressEvent error(String message) {
        return new SyncProgressEvent("error", 0, 0, 0, message, null);
    }
}
