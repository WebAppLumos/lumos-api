package com.group4.lumos_api.dashboard.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DashboardWidgetItemRequest {
    @NotBlank
    private String id;

    private boolean visible;
}
