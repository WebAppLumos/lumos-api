package com.group4.lumos_api.entry.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class EntryResponse {
    private Long id;
    private Long timetableId;
    private Long courseId;
    private String courseTitle;
    private String classroom;
    private String professor;
    private Short dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
