package com.group4.lumos_api.semester_grades.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SemesterGradeItemDto {

    private final Long semesterGradeId;
    private final Integer academicYear;
    private final String termCode;
    private final String termName;
    private final String label;
    private final Integer completedCredits;
    private final Integer registeredCredits;
    private final Double gpa;
    private final Boolean academicWarning;
    private final LocalDateTime syncedAt;
}
