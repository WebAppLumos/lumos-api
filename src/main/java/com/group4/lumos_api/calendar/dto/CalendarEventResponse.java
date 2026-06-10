package com.group4.lumos_api.calendar.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarEventResponse {

    private Long scheduleId;
    private String title;
    private String content;
    private LocalDate date;
    private String userId; // 반환할 때도 객체가 아닌 가독성 좋은 String으로 내려줍니다.
    private Boolean isCompleted;
    private String category;
    private String priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}