package com.group4.lumos_api.note.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.group4.lumos_api.note.dto.NotePinRequest;
import com.group4.lumos_api.note.dto.NoteRequest;
import com.group4.lumos_api.note.dto.NoteResponse;
import com.group4.lumos_api.note.service.NoteService;

import java.util.List;

@RestController
@RequestMapping("/api/semesters/{semesterId}/courses/{courseId}/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    public ResponseEntity<NoteResponse> createNote(
            @PathVariable Long semesterId,
            @PathVariable Long courseId,
            @Valid @RequestBody NoteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(noteService.createNote(semesterId, courseId, request));
    }

    @GetMapping
    public ResponseEntity<List<NoteResponse>> getNotes(
            @PathVariable Long semesterId,
            @PathVariable Long courseId,
            @RequestParam(required = false, name = "q") String keyword) {
        return ResponseEntity.ok(noteService.getNotes(semesterId, courseId, keyword));
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<NoteResponse> getNote(
            @PathVariable Long semesterId,
            @PathVariable Long courseId,
            @PathVariable Long noteId) {
        return ResponseEntity.ok(noteService.getNote(semesterId, courseId, noteId));
    }

    @PatchMapping("/{noteId}")
    public ResponseEntity<NoteResponse> updateNote(
            @PathVariable Long semesterId,
            @PathVariable Long courseId,
            @PathVariable Long noteId,
            @RequestBody NoteRequest request) {
        return ResponseEntity.ok(noteService.updateNote(semesterId, courseId, noteId, request));
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNote(
            @PathVariable Long semesterId,
            @PathVariable Long courseId,
            @PathVariable Long noteId) {
        noteService.deleteNote(semesterId, courseId, noteId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{noteId}/pin")
    public ResponseEntity<NoteResponse> setPinned(
            @PathVariable Long semesterId,
            @PathVariable Long courseId,
            @PathVariable Long noteId,
            @RequestBody NotePinRequest request) {
        return ResponseEntity.ok(noteService.setPinned(semesterId, courseId, noteId, request));
    }
}
