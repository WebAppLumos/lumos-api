package com.group4.lumos_api.assignment.service;

import com.group4.lumos_api.assignment.dto.AssignmentCreateRequest;
import com.group4.lumos_api.assignment.dto.AssignmentUpdateRequest;
import com.group4.lumos_api.assignment.dto.AssignmentResponse;
import com.group4.lumos_api.assignment.entity.Assignment;
import com.group4.lumos_api.assignment.repository.AssignmentRepository;
import jakarta.persistence.EntityNotFoundException;
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
    public List<AssignmentResponse> getAllAssignments(String userId) {
        return assignmentRepository.findAllByUserId(userId).stream()
                .map(AssignmentResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public AssignmentResponse createAssignment(String userId, AssignmentCreateRequest request) {
        if (assignmentRepository.existsByUserIdAndTitleAndCourse(userId, request.getTitle(), request.getCourse())) {
            throw new IllegalArgumentException("동일한 과제가 있습니다.");
        }
        Assignment assignment = Assignment.builder()
                .userId(userId)
                .course(request.getCourse())
                .title(request.getTitle())
                .deadline(request.getDeadline())
                .isCompleted(request.getIsCompleted() != null ? request.getIsCompleted() : false)
                .build();
        Assignment saved = assignmentRepository.save(assignment);
        return AssignmentResponse.from(saved);
    }

    @Transactional
    public AssignmentResponse updateAssignment(Long id, String userId, AssignmentUpdateRequest request) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 과제가 존재하지 않습니다. id=" + id));
                
        if (!assignment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("해당 과제에 대한 수정 권한이 없습니다.");
        }
                
        boolean isCourseChanged = request.getCourse() != null && !request.getCourse().equals(assignment.getCourse());
        boolean isTitleChanged = request.getTitle() != null && !request.getTitle().equals(assignment.getTitle());
        
        if (isCourseChanged || isTitleChanged) {
            String targetCourse = request.getCourse() != null ? request.getCourse() : assignment.getCourse();
            String targetTitle = request.getTitle() != null ? request.getTitle() : assignment.getTitle();
            if (assignmentRepository.existsByUserIdAndTitleAndCourse(userId, targetTitle, targetCourse)) {
                throw new IllegalArgumentException("동일한 과제가 있습니다.");
            }
        }
        
        if (request.getCourse() != null) assignment.setCourse(request.getCourse());
        if (request.getTitle() != null) assignment.setTitle(request.getTitle());
        if (request.getDeadline() != null) assignment.setDeadline(request.getDeadline());
        if (request.getIsCompleted() != null) assignment.setCompleted(request.getIsCompleted());
        
        return AssignmentResponse.from(assignment);
    }

    @Transactional
    public void deleteAssignment(Long id, String userId) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 과제가 존재하지 않습니다. id=" + id));
                
        if (!assignment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("해당 과제에 대한 삭제 권한이 없습니다.");
        }
        
        assignmentRepository.delete(assignment);
    }
}