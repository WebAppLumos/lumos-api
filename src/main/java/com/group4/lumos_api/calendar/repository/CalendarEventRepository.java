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
}
