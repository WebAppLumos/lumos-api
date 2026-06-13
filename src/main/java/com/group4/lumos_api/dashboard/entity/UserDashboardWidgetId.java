package com.group4.lumos_api.dashboard.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDashboardWidgetId implements Serializable {

    @Column(name = "user_id")
    private String userId;

    @Column(name = "widget_key")
    private String widgetKey;
}
