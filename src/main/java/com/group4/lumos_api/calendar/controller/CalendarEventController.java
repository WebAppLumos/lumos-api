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
@RequestMapping("/api/calendar-events")
@Validated
public class CalendarEventController {

    private final CalendarEventService service;

    public CalendarEventController(CalendarEventService service) {
        this.service = service;
    }

    @GetMapping
    public List<CalendarEventResponse> getEvents(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate date) {
        
        List<CalendarEvent> events;
        
        if (date != null) {
            events = service.getEventsByDate(date);
        } else if (studentId != null) {
            // 학생 ID가 있으면 학사 일정 + 내 일정 조회
            events = service.getMyAndGlobalEvents(studentId);
        } else {
            // 아무 조건 없으면 전체 조회 (관리자용 혹은 테스트용)
            events = service.getAllEvents();
        }
        
        return events.stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public CalendarEventResponse getEvent(@PathVariable Long id) {
        return toResponse(service.getEvent(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CalendarEventResponse createEvent(@Valid @RequestBody CalendarEventRequest request) {
        return toResponse(service.createEvent(request));
    }

    @PutMapping("/{id}")
    public CalendarEventResponse updateEvent(@PathVariable Long id,
                                             @Valid @RequestBody CalendarEventRequest request) {
        return toResponse(service.updateEvent(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable Long id) {
        service.deleteEvent(id);
    }

    private CalendarEventResponse toResponse(CalendarEvent entity) {
        return CalendarEventResponse.builder()
                .scheduleId(entity.getScheduleId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .date(entity.getDate())
                .studentId(entity.getStudentId())
                .build();
    }
}
