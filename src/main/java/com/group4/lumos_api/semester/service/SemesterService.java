package com.group4.lumos_api.semester.service;

import com.group4.lumos_api.semester.dto.SemesterRequest;
import com.group4.lumos_api.semester.dto.SemesterResponse;
import com.group4.lumos_api.semester.entity.Semester;
import com.group4.lumos_api.semester.repository.SemesterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SemesterService {
    
    private final SemesterRepository semesterRepository;
    
    /**
     * 새 학기 생성
     */
    public SemesterResponse createSemester(SemesterRequest request) {
        Semester semester = new Semester();
        semester.setTitle(request.getTitle());
        semester.setStartDate(request.getStartDate());
        semester.setEndDate(request.getEndDate());
        semester.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        
        Semester saved = semesterRepository.save(semester);
        return convertToResponse(saved);
    }
    
    /**
     * 학기 목록 조회
     */
    @Transactional(readOnly = true)
    public List<SemesterResponse> getAllSemesters() {
        return semesterRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 특정 학기 상세 조회
     */
    @Transactional(readOnly = true)
    public SemesterResponse getSemesterById(Long id) {
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("학기를 찾을 수 없습니다. ID: " + id));
        return convertToResponse(semester);
    }
    
    /**
     * 학기 정보 수정
     */
    public SemesterResponse updateSemester(Long id, SemesterRequest request) {
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("학기를 찾을 수 없습니다. ID: " + id));
        
        if (request.getTitle() != null) {
            semester.setTitle(request.getTitle());
        }
        if (request.getStartDate() != null) {
            semester.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            semester.setEndDate(request.getEndDate());
        }
        if (request.getIsActive() != null) {
            semester.setIsActive(request.getIsActive());
        }
        
        Semester updated = semesterRepository.save(semester);
        return convertToResponse(updated);
    }
    
    /**
     * 학기 삭제
     */
    public void deleteSemester(Long id) {
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("학기를 찾을 수 없습니다. ID: " + id));
        semesterRepository.delete(semester);
    }
    
    /**
     * Entity를 Response DTO로 변환
     */
    private SemesterResponse convertToResponse(Semester semester) {
        return new SemesterResponse(
                semester.getId(),
                semester.getTitle(),
                semester.getStartDate(),
                semester.getEndDate(),
                semester.getIsActive(),
                semester.getCreatedAt(),
                semester.getUpdatedAt()
        );
    }
}
