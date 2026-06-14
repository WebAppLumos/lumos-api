package com.group4.lumos_api.sync.model;

public record ParsedSemesterGrade(
        int academicYear,
        String termCode,
        String termName,
        int completedCredits,
        int registeredCredits,
        double gpa,
        boolean academicWarning
) {
}
