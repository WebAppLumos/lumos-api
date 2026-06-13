package com.group4.lumos_api.dashboard.repository;

import com.group4.lumos_api.dashboard.entity.UserDashboardWidget;
import com.group4.lumos_api.dashboard.entity.UserDashboardWidgetId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserDashboardWidgetRepository extends JpaRepository<UserDashboardWidget, UserDashboardWidgetId> {

    List<UserDashboardWidget> findAllById_UserIdOrderBySortOrderAsc(String userId);

    void deleteById_UserId(String userId);
}
