package com.group4.lumos_api.entry.controller;

import com.group4.lumos_api.entry.dto.EntryCreateRequest;
import com.group4.lumos_api.entry.dto.EntryResponse;
import com.group4.lumos_api.entry.service.TimetableEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/semesters/{semesterId}/timetables/{timetableId}/entries")
@RequiredArgsConstructor
public class TimetableEntryController {

    private final TimetableEntryService entryService;

    @PostMapping
    public ResponseEntity<EntryResponse> createEntry(
            @PathVariable Long semesterId,
            @PathVariable Long timetableId,
            @Valid @RequestBody EntryCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(entryService.createEntry(semesterId, timetableId, request));
    }

    @GetMapping
    public ResponseEntity<List<EntryResponse>> getEntries(
            @PathVariable Long semesterId,
            @PathVariable Long timetableId) {
        return ResponseEntity.ok(entryService.getEntries(semesterId, timetableId));
    }

    @DeleteMapping("/{entryId}")
    public ResponseEntity<Void> deleteEntry(
            @PathVariable Long semesterId,
            @PathVariable Long timetableId,
            @PathVariable Long entryId) {
        entryService.deleteEntry(semesterId, timetableId, entryId);
        return ResponseEntity.noContent().build();
    }
}
