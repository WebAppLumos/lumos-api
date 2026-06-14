package com.group4.lumos_api.sync.dto;

import lombok.Getter;

@Getter
public class GradeSyncResponse {

    private final int semesterCount;
    private final int totalCompletedCredits;
    private final double averageGpa;
    private final int academicWarningCount;

    public GradeSyncResponse(int semesterCount, int totalCompletedCredits, double averageGpa, int academicWarningCount) {
        this.semesterCount = semesterCount;
        this.totalCompletedCredits = totalCompletedCredits;
        this.averageGpa = averageGpa;
        this.academicWarningCount = academicWarningCount;
    }
}
