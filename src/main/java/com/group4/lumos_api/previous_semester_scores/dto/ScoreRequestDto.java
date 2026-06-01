package com.group4.lumos_api.previous_semester_scores.dto;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ScoreRequestDto {
    private Double score;
    private LocalDate year;
    private String semester;
}
