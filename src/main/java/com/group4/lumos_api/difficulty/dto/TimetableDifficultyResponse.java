package com.group4.lumos_api.difficulty.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TimetableDifficultyResponse {
    private Long timetableId;
    private Integer courseCount;
    private Integer ratedCourseCount;
    private Double averageLevel;
}
