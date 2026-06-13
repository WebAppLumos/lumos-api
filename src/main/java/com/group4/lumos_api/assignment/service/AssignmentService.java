package com.group4.lumos_api.assignmrnt.service;

import com.group4.lumos_api.assignmrnt.dto.AssignmentRequest;
import com.group4.lumos_api.assignmrnt.dto.AssignmentResponse;
import com.group4.lumos_api.assignmrnt.entity.Assignment;
import com.group4.lumos_api.assignmrnt.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentService {
    
    private final AssignmentRepository assignmentRepository;

    @Transactional(readOnly = true)
    public List<AssignmentResponse> getAllAssignments() {
        return assignmentRepository.findAll().stream()
                .map(AssignmentResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public AssignmentResponse createAssignment(AssignmentRequest request) {
        if (assignmentRepository.existsByTitleAndCourse(request.getTitle(), request.getCourse())) {
            throw new IllegalArgumentException("동일한 과제가 있습니다.");
        }
        Assignment assignment = Assignment.builder()
                .course(request.getCourse())
                .title(request.getTitle())
                .deadline(request.getDeadline())
                .isCompleted(request.getIsCompleted() != null ? request.getIsCompleted() : false)
                .build();
        Assignment saved = assignmentRepository.save(assignment);
        return AssignmentResponse.from(saved);
    }

    @Transactional
    public AssignmentResponse updateAssignment(Long id, AssignmentRequest request) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 과제가 존재하지 않습니다. id=" + id));
        boolean isCourseChanged = request.getCourse() != null && !request.getCourse().equals(assignment.getCourse());
        boolean isTitleChanged = request.getTitle() != null && !request.getTitle().equals(assignment.getTitle());
        if (isCourseChanged || isTitleChanged) {
            String targetCourse = request.getCourse() != null ? request.getCourse() : assignment.getCourse();
            String targetTitle = request.getTitle() != null ? request.getTitle() : assignment.getTitle();
            if (assignmentRepository.existsByTitleAndCourse(targetTitle, targetCourse)) {
                throw new IllegalArgumentException("동일한 과제가 있습니다.");
            }
        }
        if (request.getCourse() != null) assignment.setCourse(request.getCourse());
        if (request.getTitle() != null) assignment.setTitle(request.getTitle());
        if (request.getDeadline() != null) assignment.setDeadline(request.getDeadline());
        if (request.getIsCompleted() != null)
            assignment.setCompleted(request.getIsCompleted());
        return AssignmentResponse.from(assignment);
    }

    @Transactional
    public void deleteAssignment(Long id) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 과제가 존재하지 않습니다. id=" + id));
        assignmentRepository.delete(assignment);
    }
}