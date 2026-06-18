package com.group4.lumos_api.dashboard.service;

import com.group4.lumos_api.common.exception.BadRequestException;
import com.group4.lumos_api.dashboard.dto.DashboardWidgetItemRequest;
import com.group4.lumos_api.dashboard.dto.DashboardWidgetResponse;
import com.group4.lumos_api.dashboard.entity.UserDashboardWidget;
import com.group4.lumos_api.dashboard.entity.UserDashboardWidgetId;
import com.group4.lumos_api.dashboard.repository.UserDashboardWidgetRepository;
import com.group4.lumos_api.user.entity.Users;
import com.group4.lumos_api.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 사용자별 대시보드 위젯 표시·순서 설정.
 * 최초 조회 시 5개 기본 위젯을 DB에 시드합니다.
 */
@Service
@RequiredArgsConstructor
public class DashboardWidgetService {

    public static final List<String> DEFAULT_WIDGET_KEYS = List.of(
            "today-timetable",
            "schedule",
            "assignment",
            "scholarship",
            "campus-map"
    );

    private final UserDashboardWidgetRepository widgetRepository;
    private final UsersRepository usersRepository;

    @Transactional
    /** 사용자 위젯 설정 조회. 없으면 5개 기본 위젯을 생성합니다. */
    public List<DashboardWidgetResponse> getWidgets(String userId) {
        List<UserDashboardWidget> widgets = widgetRepository.findAllById_UserIdOrderBySortOrderAsc(userId);
        if (widgets.isEmpty()) {
            widgets = createDefaultWidgets(userId);
        }
        return widgets.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    /** 위젯 표시/순서 일괄 저장. 5개 위젯 키가 모두 포함돼야 합니다. */
    public List<DashboardWidgetResponse> updateWidgets(String userId, List<DashboardWidgetItemRequest> items) {
        validateWidgetItems(items);

        List<UserDashboardWidget> widgets = widgetRepository.findAllById_UserIdOrderBySortOrderAsc(userId);
        if (widgets.isEmpty()) {
            widgets = createDefaultWidgets(userId);
        }

        Map<String, UserDashboardWidget> byKey = widgets.stream()
                .collect(Collectors.toMap(widget -> widget.getId().getWidgetKey(), Function.identity()));

        for (int i = 0; i < items.size(); i++) {
            DashboardWidgetItemRequest item = items.get(i);
            UserDashboardWidget widget = byKey.get(item.getId());
            if (widget == null) {
                throw new BadRequestException("알 수 없는 위젯입니다: " + item.getId());
            }
            widget.setSortOrder(i);
            widget.setVisible(item.isVisible());
        }

        return widgetRepository.findAllById_UserIdOrderBySortOrderAsc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    /** 회원 탈퇴 시 해당 사용자의 위젯 설정을 삭제합니다. */
    public void deleteWidgetsByUserId(String userId) {
        widgetRepository.deleteById_UserId(userId);
    }

    /** 최초 가입·조회 시 기본 5개 위젯 행을 DB에 생성합니다. */
    private List<UserDashboardWidget> createDefaultWidgets(String userId) {
        Users user = usersRepository.getReferenceById(userId);
        List<UserDashboardWidget> widgets = new ArrayList<>();

        for (int i = 0; i < DEFAULT_WIDGET_KEYS.size(); i++) {
            String widgetKey = DEFAULT_WIDGET_KEYS.get(i);
            UserDashboardWidget widget = new UserDashboardWidget();
            widget.setId(new UserDashboardWidgetId(userId, widgetKey));
            widget.setUser(user);
            widget.setSortOrder(i);
            widget.setVisible(true);
            widgets.add(widget);
        }

        return widgetRepository.saveAll(widgets);
    }

    /** 요청에 5개 위젯 키가 빠짐없이 포함됐는지 검증합니다. */
    private void validateWidgetItems(List<DashboardWidgetItemRequest> items) {
        Set<String> requestedKeys = items.stream()
                .map(DashboardWidgetItemRequest::getId)
                .collect(Collectors.toSet());

        if (requestedKeys.size() != DEFAULT_WIDGET_KEYS.size()
                || !requestedKeys.containsAll(DEFAULT_WIDGET_KEYS)) {
            throw new BadRequestException("위젯 구성이 올바르지 않습니다.");
        }
    }

    private DashboardWidgetResponse toResponse(UserDashboardWidget widget) {
        return new DashboardWidgetResponse(
                widget.getId().getWidgetKey(),
                widget.getSortOrder(),
                widget.isVisible()
        );
    }
}
