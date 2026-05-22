package com.group4.lumos_api.previous_semester_scores.controller;

import com.group4.lumos_api.previous_semester_scores.dto.ScoreRequestDto;
import com.group4.lumos_api.previous_semester_scores.dto.ScoreResponseDto;
import com.group4.lumos_api.previous_semester_scores.service.PreviousSemesterScoresService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PreviousSemesterScoresController {

    private final PreviousSemesterScoresService scoresService;

    @GetMapping("/users/{userId}/previous-semester-scores")
    public ResponseEntity<List<ScoreResponseDto>> getScoresByUser(@PathVariable String userId) {
        return ResponseEntity.ok(scoresService.getScoresByUser(userId));
    }

    @PostMapping("/users/{userId}/previous-semester-scores")
    public ResponseEntity<ScoreResponseDto> addScore(
            @PathVariable String userId,
            @RequestBody ScoreRequestDto dto) {
        return ResponseEntity.ok(scoresService.addScore(userId, dto));
    }

    @PatchMapping("/previous-semester-scores/{scoreId}")
    public ResponseEntity<ScoreResponseDto> updateScore(
            @PathVariable Long scoreId,
            @RequestBody ScoreRequestDto dto) {
        return ResponseEntity.ok(scoresService.updateScore(scoreId, dto));
    }

    @DeleteMapping("/previous-semester-scores/{scoreId}")
    public ResponseEntity<Void> deleteScore(@PathVariable Long scoreId) {
        scoresService.deleteScore(scoreId);
        return ResponseEntity.noContent().build();
    }
}
