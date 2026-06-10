package com.group4.lumos_api.note.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.group4.lumos_api.common.security.CurrentUser;
import com.group4.lumos_api.note.dto.NotePinRequest;
import com.group4.lumos_api.note.dto.NoteRequest;
import com.group4.lumos_api.note.dto.NoteResponse;
import com.group4.lumos_api.note.service.NoteService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @PostMapping("/api/courses/{courseId}/notes")
    public ResponseEntity<NoteResponse> createNote(
            @CurrentUser String userId,
            @PathVariable Long courseId,
            @Valid @RequestBody NoteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(noteService.createNote(userId, courseId, request));
    }

    @GetMapping("/api/courses/{courseId}/notes")
    public ResponseEntity<List<NoteResponse>> getNotes(
            @CurrentUser String userId,
            @PathVariable Long courseId,
            @RequestParam(required = false, name = "q") String keyword) {
        return ResponseEntity.ok(noteService.getNotes(userId, courseId, keyword));
    }

    @GetMapping("/api/notes/{noteId}")
    public ResponseEntity<NoteResponse> getNote(
            @CurrentUser String userId,
            @PathVariable Long noteId) {
        return ResponseEntity.ok(noteService.getNote(userId, noteId));
    }

    @PatchMapping("/api/notes/{noteId}")
    public ResponseEntity<NoteResponse> updateNote(
            @CurrentUser String userId,
            @PathVariable Long noteId,
            @RequestBody NoteRequest request) {
        return ResponseEntity.ok(noteService.updateNote(userId, noteId, request));
    }

    @DeleteMapping("/api/notes/{noteId}")
    public ResponseEntity<Void> deleteNote(
            @CurrentUser String userId,
            @PathVariable Long noteId) {
        noteService.deleteNote(userId, noteId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/api/notes/{noteId}/pin")
    public ResponseEntity<NoteResponse> setPinned(
            @CurrentUser String userId,
            @PathVariable Long noteId,
            @RequestBody NotePinRequest request) {
        return ResponseEntity.ok(noteService.setPinned(userId, noteId, request));
    }
}
