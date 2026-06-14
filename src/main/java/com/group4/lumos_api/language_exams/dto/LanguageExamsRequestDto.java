package com.group4.lumos_api.language_exams.dto;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LanguageExamsRequestDto {
    private String examCategory;
    private String score;
    private LocalDate examDate;
    private Integer year;
    private String semester;
    private LocalDate expiryDate;
    private String userId;
}
