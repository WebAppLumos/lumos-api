package com.group4.lumos_api.calendar.service;

import com.group4.lumos_api.calendar.dto.CalendarEventRequest;
import com.group4.lumos_api.calendar.entity.CalendarEvent;
import com.group4.lumos_api.calendar.entity.EventCategory;
import com.group4.lumos_api.calendar.entity.EventPriority;
import com.group4.lumos_api.calendar.repository.CalendarEventRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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

    public List<CalendarEvent> getAllEvents() {
        return repository.findAll();
    }

    // 학사 일정 및 특정 사용자의 일정을 함께 가져오기
    public List<CalendarEvent> getMyAndGlobalEvents(String userId) {
        return repository.findByUserIsNullOrUser_UserId(userId);
    }

    public CalendarEvent getEvent(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "일정을 찾을 수 없습니다."));
    }

    public List<CalendarEvent> getEventsByDate(LocalDate date) {
        return repository.findAllByDate(date);
    }

    // 학사 일정만 조회
    public List<CalendarEvent> getAcademicEvents() {
        return repository.findByUserIsNull();
    }

    // 통합 필터 검색
    public List<CalendarEvent> searchEvents(String userId, LocalDate date, String keyword, String type) {
        return repository.findByFilters(date, keyword, userId, type);
    }

    public CalendarEvent createEvent(CalendarEventRequest request) {
        // 일반 사용자가 학사 일정(null)을 생성하지 못하도록 방지
        if (request.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "일반 사용자는 학사 일정을 생성할 수 없습니다.");
        }

        com.group4.lumos_api.user.entity.Users user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        
        CalendarEvent event = CalendarEvent.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .date(request.getDate())
                .user(user)
                .isCompleted(false) // 생성 시에는 무조건 false로 고정
                .category(request.getCategory() != null ? EventCategory.valueOf(request.getCategory()) : EventCategory.OTHER)
                .priority(request.getPriority() != null ? EventPriority.valueOf(request.getPriority()) : EventPriority.MEDIUM)
                .build();
        return repository.save(event);
    }

    public CalendarEvent updateEvent(Long id, CalendarEventRequest request) {
        CalendarEvent event = getEvent(id);
        
        // 학사 일정(user가 null)은 수정할 수 없도록 제한
        if (event.getUser() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "학사 일정은 수정할 수 없습니다.");
        }

        // 수정한 결과가 학사 일정(null)이 되지 않도록 방지
        if (request.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "일정을 학사 일정으로 변경할 수 없습니다.");
        }

        com.group4.lumos_api.user.entity.Users user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        event.setTitle(request.getTitle());
        event.setContent(request.getContent());
        event.setDate(request.getDate());
        event.setUser(user);
        event.setCompleted(request.isCompleted());
        
        if (request.getCategory() != null) {
            event.setCategory(EventCategory.valueOf(request.getCategory()));
        }
        if (request.getPriority() != null) {
            event.setPriority(EventPriority.valueOf(request.getPriority()));
        }
        
        return repository.save(event);
    }

    // 완료 상태 토글 (체크박스 클릭 시)
    public CalendarEvent toggleCompletion(Long id) {
        CalendarEvent event = getEvent(id);
        
        if (event.getUser() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "학사 일정의 상태는 변경할 수 없습니다.");
        }
        
        event.setCompleted(!event.isCompleted());
        return repository.save(event);
    }

    public void deleteEvent(Long id) {
        CalendarEvent event = getEvent(id);
        
        // 학사 일정(user가 null)은 삭제할 수 없도록 제한
        if (event.getUser() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "학사 일정은 삭제할 수 없습니다.");
        }
        
        repository.delete(event);
    }
}
