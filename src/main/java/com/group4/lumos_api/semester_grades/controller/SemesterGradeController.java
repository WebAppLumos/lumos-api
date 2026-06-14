package com.group4.lumos_api.semester_grades.controller;

import com.group4.lumos_api.common.security.CurrentUser;
import com.group4.lumos_api.semester_grades.dto.SemesterGradeSummaryDto;
import com.group4.lumos_api.semester_grades.service.SemesterGradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/me/semester-grades")
@RequiredArgsConstructor
public class SemesterGradeController {

    private final SemesterGradeService semesterGradeService;

    @GetMapping
    public ResponseEntity<SemesterGradeSummaryDto> getMySemesterGrades(@CurrentUser String userId) {
        return ResponseEntity.ok(semesterGradeService.getSummaryForUser(userId));
    }
}
