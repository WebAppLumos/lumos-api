package com.group4.lumos_api.calendar.service;

import com.group4.lumos_api.calendar.dto.CalendarEventRequest;
import com.group4.lumos_api.calendar.entity.CalendarEvent;
import com.group4.lumos_api.calendar.repository.CalendarEventRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class CalendarEventService {

    private final CalendarEventRepository repository;

    public CalendarEventService(CalendarEventRepository repository) {
        this.repository = repository;
    }

    public List<CalendarEvent> getAllEvents() {
        return repository.findAll();
    }

    // 학사 일정 및 특정 학생의 일정을 함께 가져오기
    public List<CalendarEvent> getMyAndGlobalEvents(Long studentId) {
        return repository.findByStudentIdIsNullOrStudentId(studentId);
    }

    public CalendarEvent getEvent(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "일정을 찾을 수 없습니다."));
    }

    public List<CalendarEvent> getEventsByDate(LocalDate date) {
        return repository.findAllByDate(date);
    }

    public CalendarEvent createEvent(CalendarEventRequest request) {
        // 일반 사용자가 학사 일정(null)을 생성하지 못하도록 방지
        if (request.getStudentId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "일반 사용자는 학사 일정을 생성할 수 없습니다.");
        }
        
        CalendarEvent event = CalendarEvent.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .date(request.getDate())
                .studentId(request.getStudentId())
                .build();
        return repository.save(event);
    }

    public CalendarEvent updateEvent(Long id, CalendarEventRequest request) {
        CalendarEvent event = getEvent(id);
        
        // 학사 일정(studentId가 null)은 수정할 수 없도록 제한
        if (event.getStudentId() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "학사 일정은 수정할 수 없습니다.");
        }

        // 수정한 결과가 학사 일정(null)이 되지 않도록 방지
        if (request.getStudentId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "일정을 학사 일정으로 변경할 수 없습니다.");
        }

        event.setTitle(request.getTitle());
        event.setContent(request.getContent());
        event.setDate(request.getDate());
        event.setStudentId(request.getStudentId());
        return repository.save(event);
    }

    public void deleteEvent(Long id) {
        CalendarEvent event = getEvent(id);
        
        // 학사 일정(studentId가 null)은 삭제할 수 없도록 제한
        if (event.getStudentId() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "학사 일정은 삭제할 수 없습니다.");
        }
        
        repository.delete(event);
    }
}
