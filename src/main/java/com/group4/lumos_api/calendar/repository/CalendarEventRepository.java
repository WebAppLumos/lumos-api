package com.group4.lumos_api.calendar.repository;

import com.group4.lumos_api.calendar.entity.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {

    List<CalendarEvent> findAllByDate(LocalDate date);

    // 학사 일정('admin') 또는 내 일정(user.userId = :userId) 조회
    @Query("SELECT e FROM CalendarEvent e WHERE e.user.userId = 'admin' OR e.user.userId = :userId")
    List<CalendarEvent> findByUserIsNullOrUser_UserId(@Param("userId") String userId);

    // 학사 일정만 조회 (user_id = 'admin')
    @Query("SELECT e FROM CalendarEvent e WHERE e.user.userId = 'admin'")
    List<CalendarEvent> findByAcademicEvents();

    // 키워드로 일정 검색
    List<CalendarEvent> findByTitleContainingOrContentContaining(String title, String content);

    @Query(value = "SELECT * FROM calendar WHERE " +
            "(CAST(:date AS date) IS NULL OR date = CAST(:date AS date)) AND " +
            "(:keyword IS NULL OR title ILIKE CONCAT('%', CAST(:keyword AS text), '%') OR content ILIKE CONCAT('%', CAST(:keyword AS text), '%')) AND " +
            "(:userId IS NULL OR user_id = CAST(:userId AS text) OR user_id = 'admin') AND " +
            "(:type IS NULL OR (:type = 'academic' AND user_id = 'admin') OR (:type = 'personal' AND user_id != 'admin')) AND " +
            "(:category IS NULL OR category = :category)",
            nativeQuery = true)
    List<CalendarEvent> findByFilters(
            @Param("date") LocalDate date,
            @Param("keyword") String keyword,
            @Param("userId") String userId,
            @Param("type") String type,
            @Param("category") String category
    );

    // 회원 탈퇴 시 해당 사용자의 일정 전체 삭제
    void deleteByUser_UserId(String userId);
}