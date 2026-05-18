package com.group4.lumos_api.course.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseUpdateRequest {

    private String name;

    private String classroom;

    private String professorName;

    private String color;
}