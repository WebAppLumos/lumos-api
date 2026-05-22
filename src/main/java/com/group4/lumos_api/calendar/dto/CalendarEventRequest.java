package com.group4.lumos_api.calendar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarEventRequest {

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    private String content;

    @NotNull(message = "날짜를 입력해주세요.")
    private LocalDate date;

    private Long studentId;

    private boolean isCompleted;

    private String category; // Enum name as String

    private String priority; // Enum name as String
}
