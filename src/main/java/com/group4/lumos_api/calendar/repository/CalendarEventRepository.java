package com.group4.lumos_api.calendar.repository;

import com.group4.lumos_api.calendar.entity.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {
    List<CalendarEvent> findAllByDate(LocalDate date);

    // 학사 일정(user is null) 또는 내 일정(user.userId = :userId) 조회
    @Query("SELECT e FROM CalendarEvent e WHERE e.user IS NULL OR e.user.userId = :userId")
    List<CalendarEvent> findByUserIsNullOrUser_UserId(@Param("userId") String userId);

    // 학사 일정만 조회 (user IS NULL)
    List<CalendarEvent> findByUserIsNull();

    // 키워드로 일정 검색 (제목 또는 내용에 포함된 경우)
    List<CalendarEvent> findByTitleContainingOrContentContaining(String title, String content);

    // 통합 필터 조회 (날짜, 키워드, 사용자ID, 타입)
    @Query(value = "SELECT * FROM calendar WHERE " +
           "(CAST(:date AS date) IS NULL OR date = CAST(:date AS date)) AND " +
           "(:keyword IS NULL OR title ILIKE CONCAT('%', CAST(:keyword AS text), '%') OR content ILIKE CONCAT('%', CAST(:keyword AS text), '%')) AND " +
           "(user_id IS NULL OR user_id = :userId) AND " +
           "(:type IS NULL OR (:type = 'academic' AND user_id IS NULL) OR (:type = 'personal' AND user_id IS NOT NULL))",
           nativeQuery = true)
    List<CalendarEvent> findByFilters(
            @Param("date") LocalDate date,
            @Param("keyword") String keyword,
            @Param("userId") String userId,
            @Param("type") String type);
}
