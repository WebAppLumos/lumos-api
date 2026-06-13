package com.group4.lumos_api.semester.service;

import com.group4.lumos_api.common.exception.BadRequestException;
import com.group4.lumos_api.common.exception.NotFoundException;
import com.group4.lumos_api.course.service.CourseService;
import com.group4.lumos_api.semester.dto.SemesterRequest;
import com.group4.lumos_api.semester.dto.SemesterResponse;
import com.group4.lumos_api.semester.entity.Semester;
import com.group4.lumos_api.semester.repository.SemesterRepository;
import com.group4.lumos_api.timetable.service.TimetableService;
import com.group4.lumos_api.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SemesterService {
    
    private final SemesterRepository semesterRepository;
    private final UsersRepository usersRepository;
    private final CourseService courseService;
    private final TimetableService timetableService;
    
    /**
     * 새 학기 생성 (현재 사용자 소유)
     */
    public SemesterResponse createSemester(String userId, SemesterRequest request) {
        Semester semester = new Semester();
        semester.setUser(usersRepository.getReferenceById(userId));
        semester.setTitle(request.getTitle());
        semester.setStartDate(request.getStartDate());
        semester.setEndDate(request.getEndDate());
        semester.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        semester.setSortOrder(nextSortOrder(userId));
        
        Semester saved = semesterRepository.save(semester);
        return convertToResponse(saved);
    }

    public List<SemesterResponse> reorderSemesters(String userId, List<Long> semesterIds) {
        List<Semester> semesters = semesterRepository.findAllByUser_UserIdOrderBySortOrderAscIdAsc(userId);
        Set<Long> ownedIds = semesters.stream().map(Semester::getId).collect(Collectors.toSet());

        if (semesterIds.size() != ownedIds.size() || !ownedIds.containsAll(semesterIds)) {
            throw new BadRequestException("학기 순서 변경 요청이 올바르지 않습니다.");
        }

        Map<Long, Semester> byId = semesters.stream()
                .collect(Collectors.toMap(Semester::getId, Function.identity()));

        for (int i = 0; i < semesterIds.size(); i++) {
            byId.get(semesterIds.get(i)).setSortOrder(i);
        }

        return semesterRepository.findAllByUser_UserIdOrderBySortOrderAscIdAsc(userId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 현재 사용자의 학기 목록 조회
     */
    @Transactional(readOnly = true)
    public List<SemesterResponse> getAllSemesters(String userId) {
        return semesterRepository.findAllByUser_UserIdOrderBySortOrderAscIdAsc(userId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 현재 사용자의 특정 학기 상세 조회
     */
    @Transactional(readOnly = true)
    public SemesterResponse getSemesterById(String userId, Long id) {
        return convertToResponse(getOwnedSemester(userId, id));
    }
    
    /**
     * 현재 사용자의 학기 정보 수정
     */
    public SemesterResponse updateSemester(String userId, Long id, SemesterRequest request) {
        Semester semester = getOwnedSemester(userId, id);
        
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
     * 현재 사용자의 학기 삭제
     */
    public void deleteSemester(String userId, Long id) {
        Semester semester = getOwnedSemester(userId, id);
        // 학기에 종속된 시간표(→배치)와 수업(→배치, 노트)을 먼저 제거한 뒤 학기를 삭제한다.
        timetableService.getTimetables(userId, id)
                .forEach(timetable -> timetableService.deleteTimetable(userId, timetable.getId()));
        courseService.getCoursesBySemesterId(userId, id)
                .forEach(course -> courseService.deleteCourse(userId, course.getId()));
        semesterRepository.delete(semester);
    }

    /**
     * 현재 사용자가 소유한 학기를 반환한다. 없거나 타인 소유면 404.
     */
    private Semester getOwnedSemester(String userId, Long id) {
        return semesterRepository.findByIdAndUser_UserId(id, userId)
                .orElseThrow(() -> new NotFoundException("학기를 찾을 수 없습니다. ID: " + id));
    }
    
    private int nextSortOrder(String userId) {
        return semesterRepository.findAllByUser_UserIdOrderBySortOrderAscIdAsc(userId)
                .stream()
                .mapToInt(Semester::getSortOrder)
                .max()
                .orElse(-1) + 1;
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
                semester.getSortOrder(),
                semester.getCreatedAt(),
                semester.getUpdatedAt()
        );
    }
}
