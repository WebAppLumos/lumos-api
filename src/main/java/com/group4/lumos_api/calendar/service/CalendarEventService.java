package com.group4.lumos_api.calendar.service;

import com.group4.lumos_api.calendar.dto.CalendarEventRequest;
import com.group4.lumos_api.calendar.entity.CalendarEvent;
import com.group4.lumos_api.calendar.entity.EventCategory;
import com.group4.lumos_api.calendar.entity.EventPriority;
import com.group4.lumos_api.calendar.repository.CalendarEventRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class CalendarEventService {

    private final CalendarEventRepository repository;
    private final com.group4.lumos_api.user.repository.UsersRepository userRepository;

    public CalendarEventService(CalendarEventRepository repository, com.group4.lumos_api.user.repository.UsersRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<CalendarEvent> getAllEvents() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<CalendarEvent> getMyAndGlobalEvents(String userId) {
        return repository.findByUserIsNullOrUser_UserId(userId);
    }

    @Transactional(readOnly = true)
    public CalendarEvent getEvent(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "일정을 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public List<CalendarEvent> getEventsByDate(LocalDate date) {
        return repository.findAllByDate(date);
    }

    @Transactional(readOnly = true)
    public List<CalendarEvent> getAcademicEvents() {
        return repository.findByAcademicEvents();
    }

    // 💡 통합 필터 검색 비즈니스 로직
    @Transactional(readOnly = true)
    public List<CalendarEvent> searchEvents(String userId, LocalDate date, String keyword, String type, String category) {
        return repository.findByFilters(date, keyword, userId, type, category);
    }

    @Transactional
    public CalendarEvent createEvent(CalendarEventRequest request) {
        if ("admin".equals(request.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "일반 사용자는 학사 일정을 생성할 수 없습니다.");
        }

        com.group4.lumos_api.user.entity.Users user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        
        CalendarEvent event = CalendarEvent.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .date(request.getDate())
                .user(user)
                .isCompleted(false)
                .category(request.getCategory() != null ? EventCategory.valueOf(request.getCategory()) : EventCategory.OTHER)
                .priority(request.getPriority() != null ? EventPriority.valueOf(request.getPriority()) : EventPriority.MEDIUM)
                .build();
        return repository.save(event);
    }

    @Transactional
    public CalendarEvent updateEvent(Long id, CalendarEventRequest request) {
        CalendarEvent event = getEvent(id);
        
        if (event.getUser() != null && "admin".equals(event.getUser().getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "학사 일정은 수정할 수 없습니다.");
        }

        if (request.getUserId() == null || "admin".equals(request.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "일정을 학사 일정으로 변경할 수 없습니다.");
        }

        com.group4.lumos_api.user.entity.Users user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        event.setTitle(request.getTitle());
        event.setContent(request.getContent());
        event.setDate(request.getDate());
        event.setUser(user);
        
        // 💡 [수정 완료] 포스트맨 바디에 isCompleted가 누락되어 와도 기존 상태를 유지하게끔 방어
        if (request.getIsCompleted() != null) {
            event.setIsCompleted(request.getIsCompleted());
        }
        
        if (request.getCategory() != null) {
            event.setCategory(EventCategory.valueOf(request.getCategory()));
        }
        if (request.getPriority() != null) {
            event.setPriority(EventPriority.valueOf(request.getPriority()));
        }
        
        return repository.save(event);
    }

    @Transactional
    public CalendarEvent toggleCompletion(Long id) {
        CalendarEvent event = getEvent(id);
        
        // 학사 일정이라도 완료 여부 체크는 가능하도록 제한 해제
        event.setIsCompleted(!event.getIsCompleted());
        return repository.save(event);
    }

    @Transactional
    public void deleteEvent(Long id) {
        CalendarEvent event = getEvent(id);
        
        if (event.getUser() != null && "admin".equals(event.getUser().getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "학사 일정은 삭제할 수 없습니다.");
        }
        
        repository.delete(event);
    }
}