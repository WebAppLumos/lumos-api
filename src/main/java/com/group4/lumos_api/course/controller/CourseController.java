package com.group4.lumos_api.course.controller;

import com.group4.lumos_api.course.dto.CourseCreateRequest;
import com.group4.lumos_api.course.dto.CourseResponse;
import com.group4.lumos_api.course.dto.CourseUpdateRequest;
import com.group4.lumos_api.course.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/semesters/{semesterId}/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    /**
     * 학기에 종속된 수업 생성
     * POST /api/semesters/{semesterId}/courses
     */
    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(
            @PathVariable Long semesterId,
            @Valid @RequestBody CourseCreateRequest request) {
        CourseResponse response = courseService.createCourse(semesterId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 학기별 수업 목록 조회
     * GET /api/semesters/{semesterId}/courses
     */
    @GetMapping
    public ResponseEntity<List<CourseResponse>> getCoursesBySemesterId(@PathVariable Long semesterId) {
        return ResponseEntity.ok(courseService.getCoursesBySemesterId(semesterId));
    }

    /**
     * 특정 수업 정보 조회
     * GET /api/semesters/{semesterId}/courses/{courseId}
     */
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResponse> getCourseById(
            @PathVariable Long semesterId,
            @PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getCourseById(semesterId, courseId));
    }

    /**
     * 수업 정보 수정
     * PATCH /api/semesters/{semesterId}/courses/{courseId}
     */
    @PatchMapping("/{courseId}")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable Long semesterId,
            @PathVariable Long courseId,
            @RequestBody CourseUpdateRequest request) {
        return ResponseEntity.ok(courseService.updateCourse(semesterId, courseId, request));
    }

    /**
     * 수업 삭제
     * DELETE /api/semesters/{semesterId}/courses/{courseId}
     */
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long semesterId,
            @PathVariable Long courseId) {
        courseService.deleteCourse(semesterId, courseId);
        return ResponseEntity.noContent().build();
    }
}
