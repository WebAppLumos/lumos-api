package com.group4.lumos_api.assignmrnt.controller;

import com.group4.lumos_api.assignmrnt.dto.AssignmentRequest;
import com.group4.lumos_api.assignmrnt.dto.AssignmentResponse;
import com.group4.lumos_api.assignmrnt.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AssignmentController {
    private final AssignmentService assignmentService;
    @GetMapping
    public ResponseEntity<List<AssignmentResponse>> getAssignments() {
        return ResponseEntity.ok(assignmentService.getAllAssignments());
    }

    @PostMapping
    public ResponseEntity<?> createAssignment(@RequestBody AssignmentRequest request) {
        try {
            return ResponseEntity.ok(assignmentService.createAssignment(request));
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateAssignment(
            @PathVariable Long id, 
            @RequestBody AssignmentRequest request) {
        try {
            return ResponseEntity.ok(assignmentService.updateAssignment(id, request));
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssignment(@PathVariable Long id) {
        assignmentService.deleteAssignment(id);
        return ResponseEntity.noContent().build();
    }
}