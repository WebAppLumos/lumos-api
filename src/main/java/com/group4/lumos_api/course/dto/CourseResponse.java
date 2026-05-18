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

    private String name;

    private String classroom;

    private String professorName;

    private String color;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}