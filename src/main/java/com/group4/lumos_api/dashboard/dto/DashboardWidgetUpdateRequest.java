package com.group4.lumos_api.dashboard.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class DashboardWidgetUpdateRequest {
    @NotEmpty
    @Valid
    private List<DashboardWidgetItemRequest> widgets;
}
