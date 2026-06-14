package com.group4.lumos_api.language_exams.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.group4.lumos_api.language_exams.dto.LanguageExamsRequestDto;
import com.group4.lumos_api.language_exams.dto.LanguageExamsResponseDto;
import com.group4.lumos_api.language_exams.service.LanguageExamsService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LanguageExamsController {

    private final LanguageExamsService languageExamsService;

    @GetMapping("/users/{userId}/language-exams")
    public ResponseEntity<List<LanguageExamsResponseDto>> getExamsByStudent(@PathVariable String userId) {
        return ResponseEntity.ok(languageExamsService.getExamsByStudent(userId));
    }

    @PostMapping("/users/{userId}/language-exams")
    public ResponseEntity<LanguageExamsResponseDto> addExam(
            @PathVariable String userId,
            @RequestBody LanguageExamsRequestDto dto) {
        dto.setUserId(userId);
        return ResponseEntity.ok(languageExamsService.addExam(dto));
    }

    @PatchMapping("/language-exams/{examId}")
    public ResponseEntity<LanguageExamsResponseDto> updateExam(
            @PathVariable Long examId, 
            @RequestBody LanguageExamsRequestDto dto) {
        return ResponseEntity.ok(languageExamsService.updateExam(examId, dto));
    }

    @DeleteMapping("/language-exams/{examId}")
    public ResponseEntity<Void> deleteExam(@PathVariable Long examId) {
        languageExamsService.deleteExam(examId);
        return ResponseEntity.noContent().build();
    }
}
