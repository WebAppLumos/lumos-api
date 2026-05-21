package com.group4.lumos_api.Language_Exams.dto;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LanguageExamsResponseDto {
    private Long examId;
    private String examCategory;
    private String score;
    private LocalDate examDate;
    private Integer year;
    private String semester;
    private LocalDate expiryDate;
    private Long studentId;
}
