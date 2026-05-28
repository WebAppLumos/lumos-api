package com.group4.lumos_api.difficulty.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class DifficultyResponse {
    private Long id;
    private Long courseId;
    private Short level;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
