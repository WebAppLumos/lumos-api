package com.group4.lumos_api.timetable.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TimetableResponse {
    private Long id;
    private Long semesterId;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
