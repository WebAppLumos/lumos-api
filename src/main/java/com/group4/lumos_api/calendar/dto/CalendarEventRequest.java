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

    private String userId; // 프론트에서 보낼 때는 여전히 String 문자열로 받습니다.

    private Boolean isCompleted;

    private String category; 

    private String priority; 
}