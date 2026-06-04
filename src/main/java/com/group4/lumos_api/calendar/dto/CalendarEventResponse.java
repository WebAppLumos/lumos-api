package com.group4.lumos_api.calendar.dto;

import lombok.*;

import java.time.LocalDate;

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
    private String userId;
    private boolean isCompleted;
    private String category;
    private String priority;
}
