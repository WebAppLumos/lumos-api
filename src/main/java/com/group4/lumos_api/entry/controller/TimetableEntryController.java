package com.group4.lumos_api.entry.controller;

import com.group4.lumos_api.common.security.CurrentUser;
import com.group4.lumos_api.entry.dto.EntryCreateRequest;
import com.group4.lumos_api.entry.dto.EntryResponse;
import com.group4.lumos_api.entry.dto.EntryUpdateRequest;
import com.group4.lumos_api.entry.service.TimetableEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TimetableEntryController {

    private final TimetableEntryService entryService;

    @PostMapping("/api/timetables/{timetableId}/entries")
    public ResponseEntity<EntryResponse> createEntry(
            @CurrentUser String userId,
            @PathVariable Long timetableId,
            @Valid @RequestBody EntryCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(entryService.createEntry(userId, timetableId, request));
    }

    @GetMapping("/api/timetables/{timetableId}/entries")
    public ResponseEntity<List<EntryResponse>> getEntries(
            @CurrentUser String userId,
            @PathVariable Long timetableId) {
        return ResponseEntity.ok(entryService.getEntries(userId, timetableId));
    }

    @PatchMapping("/api/entries/{entryId}")
    public ResponseEntity<EntryResponse> updateEntry(
            @CurrentUser String userId,
            @PathVariable Long entryId,
            @Valid @RequestBody EntryUpdateRequest request) {
        return ResponseEntity.ok(entryService.updateEntry(userId, entryId, request));
    }

    @DeleteMapping("/api/entries/{entryId}")
    public ResponseEntity<Void> deleteEntry(
            @CurrentUser String userId,
            @PathVariable Long entryId) {
        entryService.deleteEntry(userId, entryId);
        return ResponseEntity.noContent().build();
    }
}
