package com.group4.lumos_api.timetable.controller;

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
@RequestMapping("/api/semesters/{semesterId}/timetables")
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableService timetableService;

    @PostMapping
    public ResponseEntity<TimetableResponse> createTimetable(
            @PathVariable Long semesterId,
            @Valid @RequestBody TimetableRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(timetableService.createTimetable(semesterId, request));
    }

    @GetMapping
    public ResponseEntity<List<TimetableResponse>> getTimetables(@PathVariable Long semesterId) {
        return ResponseEntity.ok(timetableService.getTimetables(semesterId));
    }

    @GetMapping("/{timetableId}")
    public ResponseEntity<TimetableResponse> getTimetable(
            @PathVariable Long semesterId,
            @PathVariable Long timetableId) {
        return ResponseEntity.ok(timetableService.getTimetable(semesterId, timetableId));
    }

    @PatchMapping("/{timetableId}")
    public ResponseEntity<TimetableResponse> updateTimetable(
            @PathVariable Long semesterId,
            @PathVariable Long timetableId,
            @RequestBody TimetableRequest request) {
        return ResponseEntity.ok(timetableService.updateTimetable(semesterId, timetableId, request));
    }

    @DeleteMapping("/{timetableId}")
    public ResponseEntity<Void> deleteTimetable(
            @PathVariable Long semesterId,
            @PathVariable Long timetableId) {
        timetableService.deleteTimetable(semesterId, timetableId);
        return ResponseEntity.noContent().build();
    }
}
