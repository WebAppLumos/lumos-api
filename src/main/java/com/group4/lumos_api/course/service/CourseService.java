package com.group4.lumos_api.course.service;

import com.group4.lumos_api.common.exception.NotFoundException;
import com.group4.lumos_api.course.dto.CourseCreateRequest;
import com.group4.lumos_api.course.dto.CourseResponse;
import com.group4.lumos_api.course.dto.CourseUpdateRequest;
import com.group4.lumos_api.course.entity.Course;
import com.group4.lumos_api.course.repository.CourseRepository;
import com.group4.lumos_api.entry.repository.TimetableEntryRepository;
import com.group4.lumos_api.note.repository.NoteRepository;
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
public class CourseService {

    private final CourseRepository courseRepository;
    private final SemesterRepository semesterRepository;
    private final TimetableEntryRepository entryRepository;
    private final NoteRepository noteRepository;

    /**
     * 학기에 속한 수업을 새로 생성한다. (학기 소유자만)
     */
    public CourseResponse createCourse(String userId, Long semesterId, CourseCreateRequest request) {
        Semester semester = getOwnedSemester(userId, semesterId);

        Course course = new Course();
        course.setSemester(semester);
        course.setTitle(request.getTitle());
        course.setCourseCode(request.getCourseCode());
        course.setProfessor(request.getProfessor());
        course.setClassroom(request.getClassroom());
        course.setCredit(request.getCredit());
        course.setDifficultyLevel(request.getDifficultyLevel());

        Course saved = courseRepository.save(course);
        return convertToResponse(saved);
    }

    /**
     * 특정 학기에 속한 수업 목록을 조회한다. (학기 소유자만)
     */
    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesBySemesterId(String userId, Long semesterId) {
        getOwnedSemester(userId, semesterId);

        return courseRepository.findAllBySemester_IdOrderByIdAsc(semesterId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 특정 수업의 상세 정보를 조회한다. (소유자만)
     */
    @Transactional(readOnly = true)
    public CourseResponse getCourseById(String userId, Long courseId) {
        return convertToResponse(getOwnedCourse(userId, courseId));
    }

    /**
     * 수업명을 포함한 핵심 정보를 수정한다. (소유자만)
     */
    public CourseResponse updateCourse(String userId, Long courseId, CourseUpdateRequest request) {
        Course course = getOwnedCourse(userId, courseId);

        if (request.getTitle() != null) {
            course.setTitle(request.getTitle());
        }
        if (request.getCourseCode() != null) {
            course.setCourseCode(request.getCourseCode());
        }
        if (request.getProfessor() != null) {
            course.setProfessor(request.getProfessor());
        }
        if (request.getClassroom() != null) {
            course.setClassroom(request.getClassroom());
        }
        if (request.getCredit() != null) {
            course.setCredit(request.getCredit());
        }
        if (request.getDifficultyLevel() != null) {
            course.setDifficultyLevel(request.getDifficultyLevel());
        }

        Course updated = courseRepository.save(course);
        return convertToResponse(updated);
    }

    /**
     * 수업을 삭제한다. (소유자만)
     */
    public void deleteCourse(String userId, Long courseId) {
        Course course = getOwnedCourse(userId, courseId);
        // 수업에 종속된 시간표 배치와 노트를 먼저 제거한 뒤 수업을 삭제한다.
        entryRepository.deleteAllByCourse_Id(courseId);
        noteRepository.deleteAllByCourse_Id(courseId);
        courseRepository.delete(course);
    }

    /**
     * 현재 사용자가 소유한 학기를 반환한다. 없거나 타인 소유면 404.
     */
    private Semester getOwnedSemester(String userId, Long semesterId) {
        return semesterRepository.findByIdAndUser_UserId(semesterId, userId)
                .orElseThrow(() -> new NotFoundException("학기를 찾을 수 없습니다. ID: " + semesterId));
    }

    /**
     * 현재 사용자가 소유한 수업을 반환한다. 없거나 타인 소유면 404.
     */
    private Course getOwnedCourse(String userId, Long courseId) {
        return courseRepository.findByIdAndSemester_User_UserId(courseId, userId)
                .orElseThrow(() -> new NotFoundException("수업을 찾을 수 없습니다. ID: " + courseId));
    }

    /**
     * 엔티티를 응답 형식으로 바꿔서 API 응답을 단순화한다.
     */
    private CourseResponse convertToResponse(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getSemester().getId(),
                course.getTitle(),
                course.getCourseCode(),
                course.getProfessor(),
                course.getClassroom(),
                course.getCredit(),
                course.getIsOnline(),
                course.getDifficultyLevel(),
                course.getCreatedAt(),
                course.getUpdatedAt()
        );
    }
}
