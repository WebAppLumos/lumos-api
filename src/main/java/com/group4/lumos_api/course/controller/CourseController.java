package com.group4.lumos_api.course.controller;

import com.group4.lumos_api.common.security.CurrentUser;
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
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    /**
     * 학기에 종속된 수업 생성
     * POST /api/semesters/{semesterId}/courses
     */
    @PostMapping("/api/semesters/{semesterId}/courses")
    public ResponseEntity<CourseResponse> createCourse(
            @CurrentUser String userId,
            @PathVariable Long semesterId,
            @Valid @RequestBody CourseCreateRequest request) {
        CourseResponse response = courseService.createCourse(userId, semesterId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 학기별 수업 목록 조회
     * GET /api/semesters/{semesterId}/courses
     */
    @GetMapping("/api/semesters/{semesterId}/courses")
    public ResponseEntity<List<CourseResponse>> getCoursesBySemesterId(
            @CurrentUser String userId,
            @PathVariable Long semesterId) {
        return ResponseEntity.ok(courseService.getCoursesBySemesterId(userId, semesterId));
    }

    /**
     * 특정 수업 정보 조회
     * GET /api/courses/{courseId}
     */
    @GetMapping("/api/courses/{courseId}")
    public ResponseEntity<CourseResponse> getCourseById(
            @CurrentUser String userId,
            @PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getCourseById(userId, courseId));
    }

    /**
     * 수업 정보 수정
     * PATCH /api/courses/{courseId}
     */
    @PatchMapping("/api/courses/{courseId}")
    public ResponseEntity<CourseResponse> updateCourse(
            @CurrentUser String userId,
            @PathVariable Long courseId,
            @Valid @RequestBody CourseUpdateRequest request) {
        return ResponseEntity.ok(courseService.updateCourse(userId, courseId, request));
    }

    /**
     * 수업 삭제
     * DELETE /api/courses/{courseId}
     */
    @DeleteMapping("/api/courses/{courseId}")
    public ResponseEntity<Void> deleteCourse(
            @CurrentUser String userId,
            @PathVariable Long courseId) {
        courseService.deleteCourse(userId, courseId);
        return ResponseEntity.noContent().build();
    }
}
