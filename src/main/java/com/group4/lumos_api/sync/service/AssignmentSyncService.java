package com.group4.lumos_api.sync.service;

import com.group4.lumos_api.assignment.dto.AssignmentResponse;
import com.group4.lumos_api.assignment.entity.Assignment;
import com.group4.lumos_api.assignment.repository.AssignmentRepository;
import com.group4.lumos_api.sync.dto.AssignmentImportItem;
import com.group4.lumos_api.sync.dto.AssignmentImportRequest;
import com.group4.lumos_api.sync.dto.AssignmentSyncResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssignmentSyncService {

    private final AssignmentRepository assignmentRepository;

    @Transactional
    public AssignmentSyncResponse importFromCtl(String userId, AssignmentImportRequest request) {
        int createdCount = 0;
        int updatedCount = 0;
        int skippedCount = 0;
        List<AssignmentResponse> savedAssignments = new ArrayList<>();

        for (AssignmentImportItem item : request.getAssignments()) {
            Optional<Assignment> existing = assignmentRepository
                    .findByUserIdAndCourseAndTitle(userId, item.getCourse(), item.getTitle());

            if (existing.isPresent()) {
                Assignment assignment = existing.get();
                boolean changed = false;

                if (!assignment.getDeadline().equals(item.getDeadline())) {
                    assignment.setDeadline(item.getDeadline());
                    changed = true;
                }

                boolean completed = Boolean.TRUE.equals(item.getIsCompleted());
                if (assignment.isCompleted() != completed) {
                    assignment.setCompleted(completed);
                    changed = true;
                }

                if (changed) {
                    updatedCount++;
                    savedAssignments.add(AssignmentResponse.from(assignment));
                } else {
                    skippedCount++;
                }
                continue;
            }

            Assignment created = Assignment.builder()
                    .userId(userId)
                    .course(item.getCourse())
                    .title(item.getTitle())
                    .deadline(item.getDeadline())
                    .isCompleted(Boolean.TRUE.equals(item.getIsCompleted()))
                    .build();
            Assignment saved = assignmentRepository.save(created);
            createdCount++;
            savedAssignments.add(AssignmentResponse.from(saved));
        }

        return new AssignmentSyncResponse(
                createdCount,
                updatedCount,
                skippedCount,
                request.getAssignments().size(),
                savedAssignments);
    }
}
