package com.group4.lumos_api.semester_grades.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class SemesterGradeSummaryDto {

    private final Integer totalCompletedCredits;
    private final Double averageGpa;
    private final Integer academicWarningCount;
    private final LocalDateTime lastSyncedAt;
    private final List<SemesterGradeItemDto> semesters;
}
