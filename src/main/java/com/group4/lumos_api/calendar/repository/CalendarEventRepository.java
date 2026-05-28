package com.group4.lumos_api.calendar.repository;

import com.group4.lumos_api.calendar.entity.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {
    List<CalendarEvent> findAllByDate(LocalDate date);

    // 학사 일정(studentId is null) 또는 내 일정(studentId = :studentId) 조회
    @Query("SELECT e FROM CalendarEvent e WHERE e.studentId IS NULL OR e.studentId = :studentId")
    List<CalendarEvent> findByStudentIdIsNullOrStudentId(@Param("studentId") Long studentId);

    // 학사 일정만 조회 (studentId IS NULL)
    List<CalendarEvent> findByStudentIdIsNull();

    // 키워드로 일정 검색 (제목 또는 내용에 포함된 경우)
    List<CalendarEvent> findByTitleContainingOrContentContaining(String title, String content);

    // 통합 필터 조회 (날짜, 키워드, 학생ID, 타입)
    @Query(value = "SELECT * FROM calendar WHERE " +
           "(CAST(:date AS date) IS NULL OR date = CAST(:date AS date)) AND " +
           "(:keyword IS NULL OR title ILIKE CONCAT('%', CAST(:keyword AS text), '%') OR content ILIKE CONCAT('%', CAST(:keyword AS text), '%')) AND " +
           "(student_id IS NULL OR student_id = :studentId) AND " +
           "(:type IS NULL OR (:type = 'academic' AND student_id IS NULL) OR (:type = 'personal' AND student_id IS NOT NULL))",
           nativeQuery = true)
    List<CalendarEvent> findByFilters(
            @Param("date") LocalDate date,
            @Param("keyword") String keyword,
            @Param("studentId") Long studentId,
            @Param("type") String type);
}
