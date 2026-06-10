package com.group4.lumos_api.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseCreateRequest {

    @NotBlank
    private String title;

    @Size(max = 8)
    private String courseCode;

    @Size(max = 50)
    private String professor;

    @Size(max = 50)
    private String classroom;

    private Short credit;

    @Min(1)
    @Max(5)
    private Short difficultyLevel;
}
