package com.group4.lumos_api.timetable.controller;

import com.group4.lumos_api.common.security.CurrentUser;
import com.group4.lumos_api.timetable.dto.TimetableRequest;
import com.group4.lumos_api.timetable.dto.TimetableResponse;
import com.group4.lumos_api.timetable.service.TimetableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableService timetableService;

    @PostMapping("/api/semesters/{semesterId}/timetables")
    public ResponseEntity<TimetableResponse> createTimetable(
            @CurrentUser String userId,
            @PathVariable Long semesterId,
            @Valid @RequestBody TimetableRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(timetableService.createTimetable(userId, semesterId, request));
    }

    @GetMapping("/api/semesters/{semesterId}/timetables")
    public ResponseEntity<List<TimetableResponse>> getTimetables(
            @CurrentUser String userId,
            @PathVariable Long semesterId) {
        return ResponseEntity.ok(timetableService.getTimetables(userId, semesterId));
    }

    @GetMapping("/api/timetables/{timetableId}")
    public ResponseEntity<TimetableResponse> getTimetable(
            @CurrentUser String userId,
            @PathVariable Long timetableId) {
        return ResponseEntity.ok(timetableService.getTimetable(userId, timetableId));
    }

    @PatchMapping("/api/timetables/{timetableId}")
    public ResponseEntity<TimetableResponse> updateTimetable(
            @CurrentUser String userId,
            @PathVariable Long timetableId,
            @RequestBody TimetableRequest request) {
        return ResponseEntity.ok(timetableService.updateTimetable(userId, timetableId, request));
    }

    @DeleteMapping("/api/timetables/{timetableId}")
    public ResponseEntity<Void> deleteTimetable(
            @CurrentUser String userId,
            @PathVariable Long timetableId) {
        timetableService.deleteTimetable(userId, timetableId);
        return ResponseEntity.noContent().build();
    }
}
