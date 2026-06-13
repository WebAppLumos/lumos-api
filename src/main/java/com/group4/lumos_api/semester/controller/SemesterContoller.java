package com.group4.lumos_api.semester.controller;

import com.group4.lumos_api.common.security.CurrentUser;
import com.group4.lumos_api.semester.dto.SemesterReorderRequest;
import com.group4.lumos_api.semester.dto.SemesterRequest;
import com.group4.lumos_api.semester.dto.SemesterResponse;
import com.group4.lumos_api.semester.service.SemesterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/semesters")
@RequiredArgsConstructor
public class SemesterContoller {
    
    private final SemesterService semesterService;
    
    /**
     * 새 학기 생성
     * POST /api/semesters
     */
    @PostMapping
    public ResponseEntity<SemesterResponse> createSemester(
            @CurrentUser String userId,
            @RequestBody SemesterRequest request) {
        SemesterResponse response = semesterService.createSemester(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 학기 목록 조회
     * GET /api/semesters
     */
    @GetMapping
    public ResponseEntity<List<SemesterResponse>> getAllSemesters(@CurrentUser String userId) {
        List<SemesterResponse> semesters = semesterService.getAllSemesters(userId);
        return ResponseEntity.ok(semesters);
    }

    /**
     * 학기 순서 변경
     * PUT /api/semesters/reorder
     */
    @PutMapping("/reorder")
    public ResponseEntity<List<SemesterResponse>> reorderSemesters(
            @CurrentUser String userId,
            @Valid @RequestBody SemesterReorderRequest request) {
        return ResponseEntity.ok(semesterService.reorderSemesters(userId, request.getSemesterIds()));
    }
    
    /**
     * 특정 학기 상세 조회
     * GET /api/semesters/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<SemesterResponse> getSemesterById(
            @CurrentUser String userId,
            @PathVariable Long id) {
        SemesterResponse response = semesterService.getSemesterById(userId, id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 학기 정보 수정
     * PATCH /api/semesters/{id}
     */
    @PatchMapping("/{id}")
    public ResponseEntity<SemesterResponse> updateSemester(
            @CurrentUser String userId,
            @PathVariable Long id,
            @RequestBody SemesterRequest request) {
        SemesterResponse response = semesterService.updateSemester(userId, id, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 학기 삭제
     * DELETE /api/semesters/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSemester(
            @CurrentUser String userId,
            @PathVariable Long id) {
        semesterService.deleteSemester(userId, id);
        return ResponseEntity.noContent().build();
    }
}
