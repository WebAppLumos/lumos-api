package com.group4.lumos_api.sync.controller;

import com.group4.lumos_api.common.security.CurrentUser;
import com.group4.lumos_api.sync.dto.TimetableImportRequest;
import com.group4.lumos_api.sync.dto.TimetableSyncRequest;
import com.group4.lumos_api.sync.dto.TimetableSyncResponse;
import com.group4.lumos_api.sync.service.TimetableSyncService;
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
public class TimetableSyncController {

    private final TimetableSyncService timetableSyncService;

    /**
     * EDWARD 자격증명(일회성)으로 개인 시간표를 가져와 DB에 저장한다.
     */
    @PostMapping("/timetable")
    public ResponseEntity<TimetableSyncResponse> syncTimetable(
            @CurrentUser String userId,
            @Valid @RequestBody TimetableSyncRequest request) {
        return ResponseEntity.ok(timetableSyncService.syncFromEdward(userId, request));
    }

    /**
     * 브라우저 확장이 EDWARD 세션으로 가져온 MML 시간표를 저장한다.
     */
    @PostMapping("/timetable/import")
    public ResponseEntity<TimetableSyncResponse> importTimetable(
            @CurrentUser String userId,
            @Valid @RequestBody TimetableImportRequest request) {
        return ResponseEntity.ok(timetableSyncService.importFromMml(userId, request));
    }
}
