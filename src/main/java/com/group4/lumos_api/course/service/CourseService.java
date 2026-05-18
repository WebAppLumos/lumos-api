package com.group4.lumos_api.course.service;

import com.group4.lumos_api.course.dto.CourseCreateRequest;
import com.group4.lumos_api.course.dto.CourseResponse;
import com.group4.lumos_api.course.dto.CourseUpdateRequest;
import com.group4.lumos_api.course.entity.Course;
import com.group4.lumos_api.course.repository.CourseRepository;
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

    /**
     * 학기에 속한 수업을 새로 생성한다.
     */
    public CourseResponse createCourse(Long semesterId, CourseCreateRequest request) {
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new RuntimeException("학기를 찾을 수 없습니다. ID: " + semesterId));

        Course course = new Course();
        course.setSemester(semester);
        course.setName(request.getName());
        course.setClassroom(request.getClassroom());
        course.setProfessorName(request.getProfessorName());
        course.setColor(request.getColor());

        Course saved = courseRepository.save(course);
        return convertToResponse(saved);
    }

    /**
     * 특정 학기에 속한 수업 목록을 조회한다.
     */
    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesBySemesterId(Long semesterId) {
        semesterRepository.findById(semesterId)
                .orElseThrow(() -> new RuntimeException("학기를 찾을 수 없습니다. ID: " + semesterId));

        return courseRepository.findAllBySemester_IdOrderByIdAsc(semesterId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 특정 수업의 상세 정보를 조회한다.
     */
    @Transactional(readOnly = true)
    public CourseResponse getCourseById(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("수업을 찾을 수 없습니다. ID: " + courseId));
        return convertToResponse(course);
    }

    /**
     * 수업명을 포함한 핵심 정보를 수정한다.
     */
    public CourseResponse updateCourse(Long courseId, CourseUpdateRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("수업을 찾을 수 없습니다. ID: " + courseId));

        if (request.getName() != null) {
            course.setName(request.getName());
        }
        if (request.getClassroom() != null) {
            course.setClassroom(request.getClassroom());
        }
        if (request.getProfessorName() != null) {
            course.setProfessorName(request.getProfessorName());
        }
        if (request.getColor() != null) {
            course.setColor(request.getColor());
        }

        Course updated = courseRepository.save(course);
        return convertToResponse(updated);
    }

    /**
     * 수업을 삭제한다.
     */
    public void deleteCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("수업을 찾을 수 없습니다. ID: " + courseId));
        courseRepository.delete(course);
    }

    /**
     * 엔티티를 응답 형식으로 바꿔서 API 응답을 단순화한다.
     */
    private CourseResponse convertToResponse(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getSemester().getId(),
                course.getName(),
                course.getClassroom(),
                course.getProfessorName(),
                course.getColor(),
                course.getCreatedAt(),
                course.getUpdatedAt()
        );
    }
}