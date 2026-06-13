package com.group4.lumos_api.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardWidgetResponse {
    private String id;
    private int sortOrder;
    private boolean visible;
}
