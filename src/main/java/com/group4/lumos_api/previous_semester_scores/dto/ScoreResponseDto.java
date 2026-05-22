package com.group4.lumos_api.previous_semester_scores.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ScoreResponseDto {
    private Long gradeId;
    private String userId;
    private Double score;
    private LocalDate year;
    private String semester;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
