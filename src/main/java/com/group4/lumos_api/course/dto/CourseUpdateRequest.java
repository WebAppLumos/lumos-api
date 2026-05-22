package com.group4.lumos_api.course.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseUpdateRequest {

    private String title;

    private String courseCode;

    private String professor;

    private String classroom;

    private Short credit;
}
