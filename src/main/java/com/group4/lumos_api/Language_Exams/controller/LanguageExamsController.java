package com.group4.lumos_api.Language_Exams.controller;

import com.group4.lumos_api.Language_Exams.dto.LanguageExamsRequestDto;
import com.group4.lumos_api.Language_Exams.dto.LanguageExamsResponseDto;
import com.group4.lumos_api.Language_Exams.service.LanguageExamsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/language-exams")
@RequiredArgsConstructor
public class LanguageExamsController {

    private final LanguageExamsService languageExamsService;

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<LanguageExamsResponseDto>> getExamsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(languageExamsService.getExamsByStudent(studentId));
    }

    @PostMapping
    public ResponseEntity<LanguageExamsResponseDto> addExam(@RequestBody LanguageExamsRequestDto dto) {
        return ResponseEntity.ok(languageExamsService.addExam(dto));
    }

    @PutMapping("/{examId}")
    public ResponseEntity<LanguageExamsResponseDto> updateExam(
            @PathVariable Long examId, 
            @RequestBody LanguageExamsRequestDto dto) {
        return ResponseEntity.ok(languageExamsService.updateExam(examId, dto));
    }

    @DeleteMapping("/{examId}")
    public ResponseEntity<Void> deleteExam(@PathVariable Long examId) {
        languageExamsService.deleteExam(examId);
        return ResponseEntity.noContent().build();
    }
}
