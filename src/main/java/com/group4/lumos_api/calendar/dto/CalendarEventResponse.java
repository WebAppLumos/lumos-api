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
    private Long studentId;
    private boolean isCompleted;
    private String category;
    private String priority;
}
