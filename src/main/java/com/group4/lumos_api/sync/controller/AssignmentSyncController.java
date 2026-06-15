package com.group4.lumos_api.sync.controller;

import com.group4.lumos_api.common.security.CurrentUser;
import com.group4.lumos_api.sync.dto.AssignmentImportRequest;
import com.group4.lumos_api.sync.dto.AssignmentSyncResponse;
import com.group4.lumos_api.sync.service.AssignmentSyncService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
public class AssignmentSyncController {

    private final AssignmentSyncService assignmentSyncService;

    /**
     * 브라우저 확장이 CTL 세션으로 가져온 진행 중 과제를 저장한다.
     */
    @PostMapping("/assignments/import")
    public ResponseEntity<AssignmentSyncResponse> importAssignments(
            @CurrentUser String userId,
            @Valid @RequestBody AssignmentImportRequest request) {
        return ResponseEntity.ok(assignmentSyncService.importFromCtl(userId, request));
    }
}
