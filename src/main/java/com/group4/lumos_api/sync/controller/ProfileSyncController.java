package com.group4.lumos_api.sync.controller;

import com.group4.lumos_api.common.security.CurrentUser;
import com.group4.lumos_api.sync.dto.ProfileImportRequest;
import com.group4.lumos_api.sync.dto.ProfileSyncResponse;
import com.group4.lumos_api.sync.service.ProfileSyncService;
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
public class ProfileSyncController {

    private final ProfileSyncService profileSyncService;

    /**
     * 브라우저 확장이 EDWARD 세션으로 가져온 학번·학년·전공을 저장한다.
     */
    @PostMapping("/profile/import")
    public ResponseEntity<ProfileSyncResponse> importProfile(
            @CurrentUser String userId,
            @Valid @RequestBody ProfileImportRequest request) {
        return ResponseEntity.ok(profileSyncService.importFromEdward(userId, request));
    }
}
