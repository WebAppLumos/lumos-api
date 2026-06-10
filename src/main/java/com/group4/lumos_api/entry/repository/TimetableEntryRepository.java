package com.group4.lumos_api.entry.repository;

import com.group4.lumos_api.entry.entity.TimetableEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimetableEntryRepository extends JpaRepository<TimetableEntry, Long> {

    List<TimetableEntry> findAllByTimetable_IdOrderByIdAsc(Long timetableId);

    Optional<TimetableEntry> findByIdAndTimetable_Semester_User_Id(Long entryId, String userId);

    boolean existsByTimetable_IdAndCourse_Id(Long timetableId, Long courseId);

    List<TimetableEntry> findAllByTimetable_IdAndDayOfWeek(Long timetableId, Short dayOfWeek);

    void deleteAllByTimetable_Id(Long timetableId);

    void deleteAllByCourse_Id(Long courseId);
}
