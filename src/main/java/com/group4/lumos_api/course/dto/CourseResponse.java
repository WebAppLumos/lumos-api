package com.group4.lumos_api.course.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {

    private Long id;

    private Long semesterId;

    private String title;

    private String courseCode;

    private String professor;

    private String classroom;

    private Short credit;

    private Boolean isOnline;

    private Short difficultyLevel;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
