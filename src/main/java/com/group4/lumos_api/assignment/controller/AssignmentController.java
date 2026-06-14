package com.group4.lumos_api.assignment.controller;

import com.group4.lumos_api.assignment.dto.AssignmentCreateRequest;
import com.group4.lumos_api.assignment.dto.AssignmentUpdateRequest;
import com.group4.lumos_api.assignment.dto.AssignmentResponse;
import com.group4.lumos_api.assignment.service.AssignmentService;
import com.group4.lumos_api.common.security.CurrentUser;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {
    
    private final AssignmentService assignmentService;
    
    @GetMapping
    public ResponseEntity<List<AssignmentResponse>> getAssignments(@CurrentUser String userId) {
        return ResponseEntity.ok(assignmentService.getAllAssignments(userId));
    }

    @PostMapping
    public ResponseEntity<?> createAssignment(
            @CurrentUser String userId,
            @Valid @RequestBody AssignmentCreateRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(assignmentService.createAssignment(userId, request));
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateAssignment(
            @PathVariable Long id,
            @CurrentUser String userId,
            @Valid @RequestBody AssignmentUpdateRequest request) {
        try {
            return ResponseEntity.ok(assignmentService.updateAssignment(id, userId, request));
        } catch (EntityNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAssignment(
            @PathVariable Long id,
            @CurrentUser String userId) {
         try {
             assignmentService.deleteAssignment(id, userId);
             return ResponseEntity.noContent().build();
         } catch (EntityNotFoundException e) {
             Map<String, String> response = new HashMap<>();
             response.put("error", e.getMessage());
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
         } catch (IllegalArgumentException e) {
             Map<String, String> response = new HashMap<>();
             response.put("error", e.getMessage());
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
         }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
