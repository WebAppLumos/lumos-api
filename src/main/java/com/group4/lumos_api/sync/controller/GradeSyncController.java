package com.group4.lumos_api.sync.controller;

import com.group4.lumos_api.common.security.CurrentUser;
import com.group4.lumos_api.sync.dto.GradeImportRequest;
import com.group4.lumos_api.sync.dto.GradeSyncResponse;
import com.group4.lumos_api.sync.service.GradeSyncService;
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
public class GradeSyncController {

    private final GradeSyncService gradeSyncService;

    /**
     * 브라우저 확장이 EDWARD 세션으로 가져온 학기 성적 SSV를 저장한다.
     */
    @PostMapping("/grades/import")
    public ResponseEntity<GradeSyncResponse> importGrades(
            @CurrentUser String userId,
            @Valid @RequestBody GradeImportRequest request) {
        return ResponseEntity.ok(gradeSyncService.importFromSsv(userId, request));
    }
}
