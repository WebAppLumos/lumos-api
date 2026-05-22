package com.group4.lumos_api.calendar.controller;

import com.group4.lumos_api.calendar.dto.CalendarEventRequest;
import com.group4.lumos_api.calendar.dto.CalendarEventResponse;
import com.group4.lumos_api.calendar.entity.CalendarEvent;
import com.group4.lumos_api.calendar.service.CalendarEventService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/calendar/events")
@Validated
public class CalendarEventController {

    private final CalendarEventService service;

    public CalendarEventController(CalendarEventService service) {
        this.service = service;
    }

    // 일정 통합 조회 (전체, 날짜별, 검색, 유형별)
    @GetMapping
    public List<CalendarEventResponse> getEvents(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type) {
        
        return service.searchEvents(studentId, date, keyword, type)
                .stream().map(this::toResponse).toList();
    }

    @GetMapping("/{eventId}")
    public CalendarEventResponse getEvent(@PathVariable("eventId") Long eventId) {
        return toResponse(service.getEvent(eventId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CalendarEventResponse createEvent(@Valid @RequestBody CalendarEventRequest request) {
        return toResponse(service.createEvent(request));
    }

    @PutMapping("/{eventId}")
    public CalendarEventResponse updateEvent(@PathVariable("eventId") Long eventId,
                                             @Valid @RequestBody CalendarEventRequest request) {
        return toResponse(service.updateEvent(eventId, request));
    }

    @DeleteMapping("/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable("eventId") Long eventId) {
        service.deleteEvent(eventId);
    }

    @PatchMapping("/{eventId}/toggle")
    public CalendarEventResponse toggleCompletion(@PathVariable("eventId") Long eventId) {
        return toResponse(service.toggleCompletion(eventId));
    }

    private CalendarEventResponse toResponse(CalendarEvent entity) {
        return CalendarEventResponse.builder()
                .scheduleId(entity.getScheduleId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .date(entity.getDate())
                .studentId(entity.getStudentId())
                .isCompleted(entity.isCompleted())
                .category(entity.getCategory() != null ? entity.getCategory().name() : null)
                .priority(entity.getPriority() != null ? entity.getPriority().name() : null)
                .build();
    }
}
