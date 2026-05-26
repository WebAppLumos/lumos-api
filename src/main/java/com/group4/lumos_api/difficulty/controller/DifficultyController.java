package com.group4.lumos_api.difficulty.controller;

import com.group4.lumos_api.difficulty.dto.DifficultyRequest;
import com.group4.lumos_api.difficulty.dto.DifficultyResponse;
import com.group4.lumos_api.difficulty.dto.TimetableDifficultyResponse;
import com.group4.lumos_api.difficulty.service.DifficultyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class DifficultyController {

    private final DifficultyService difficultyService;

    @PostMapping("/api/semesters/{semesterId}/courses/{courseId}/difficulty")
    public ResponseEntity<DifficultyResponse> setDifficulty(
            @PathVariable Long semesterId,
            @PathVariable Long courseId,
            @Valid @RequestBody DifficultyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(difficultyService.setDifficulty(semesterId, courseId, request));
    }

    @GetMapping("/api/semesters/{semesterId}/courses/{courseId}/difficulty")
    public ResponseEntity<DifficultyResponse> getDifficulty(
            @PathVariable Long semesterId,
            @PathVariable Long courseId) {
        return ResponseEntity.ok(difficultyService.getDifficulty(semesterId, courseId));
    }

    @GetMapping("/api/semesters/{semesterId}/timetables/{timetableId}/difficulty")
    public ResponseEntity<TimetableDifficultyResponse> getTimetableAverage(
            @PathVariable Long semesterId,
            @PathVariable Long timetableId) {
        return ResponseEntity.ok(difficultyService.getTimetableAverage(semesterId, timetableId));
    }
}
