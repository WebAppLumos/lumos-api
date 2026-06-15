package com.group4.lumos_api.sync.dto;

import com.group4.lumos_api.assignment.dto.AssignmentResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class AssignmentSyncResponse {

    private final int createdCount;
    private final int updatedCount;
    private final int skippedCount;
    private final int fetchedCount;
    private final List<AssignmentResponse> assignments;

    public AssignmentSyncResponse(
            int createdCount,
            int updatedCount,
            int skippedCount,
            int fetchedCount,
            List<AssignmentResponse> assignments) {
        this.createdCount = createdCount;
        this.updatedCount = updatedCount;
        this.skippedCount = skippedCount;
        this.fetchedCount = fetchedCount;
        this.assignments = assignments;
    }
}
