package com.group4.lumos_api.course.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseCreateRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String classroom;

    @NotBlank
    private String professorName;

    @NotBlank
    private String color;
}