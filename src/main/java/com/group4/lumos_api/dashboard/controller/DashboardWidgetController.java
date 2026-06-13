package com.group4.lumos_api.dashboard.controller;

import com.group4.lumos_api.common.security.CurrentUser;
import com.group4.lumos_api.dashboard.dto.DashboardWidgetResponse;
import com.group4.lumos_api.dashboard.dto.DashboardWidgetUpdateRequest;
import com.group4.lumos_api.dashboard.service.DashboardWidgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users/me/dashboard/widgets")
@RequiredArgsConstructor
public class DashboardWidgetController {

    private final DashboardWidgetService dashboardWidgetService;

    @GetMapping
    public ResponseEntity<List<DashboardWidgetResponse>> getWidgets(@CurrentUser String userId) {
        return ResponseEntity.ok(dashboardWidgetService.getWidgets(userId));
    }

    @PutMapping
    public ResponseEntity<List<DashboardWidgetResponse>> updateWidgets(
            @CurrentUser String userId,
            @Valid @RequestBody DashboardWidgetUpdateRequest request) {
        return ResponseEntity.ok(dashboardWidgetService.updateWidgets(userId, request.getWidgets()));
    }
}
