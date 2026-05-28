package com.group4.lumos_api.entry.service;

import com.group4.lumos_api.course.entity.Course;
import com.group4.lumos_api.course.repository.CourseRepository;
import com.group4.lumos_api.entry.dto.EntryCreateRequest;
import com.group4.lumos_api.entry.dto.EntryResponse;
import com.group4.lumos_api.entry.entity.TimetableEntry;
import com.group4.lumos_api.entry.repository.TimetableEntryRepository;
import com.group4.lumos_api.timetable.entity.Timetable;
import com.group4.lumos_api.timetable.service.TimetableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TimetableEntryService {

    private final TimetableEntryRepository entryRepository;
    private final CourseRepository courseRepository;
    private final TimetableService timetableService;

    public EntryResponse createEntry(Long semesterId, Long timetableId, EntryCreateRequest request) {
        Timetable timetable = timetableService.getTimetableEntity(semesterId, timetableId);
        Course course = courseRepository.findByIdAndSemester_Id(request.getCourseId(), semesterId)
                .orElseThrow(() -> new RuntimeException("수업을 찾을 수 없습니다. ID: " + request.getCourseId()));

        if (entryRepository.existsByTimetable_IdAndCourse_Id(timetableId, request.getCourseId())) {
            throw new RuntimeException("이미 시간표에 배치된 수업입니다. ID: " + request.getCourseId());
        }

        TimetableEntry entry = new TimetableEntry();
        entry.setTimetable(timetable);
        entry.setCourse(course);
        entry.setDayOfWeek(request.getDayOfWeek());
        entry.setStartTime(request.getStartTime());
        entry.setEndTime(request.getEndTime());
        return toResponse(entryRepository.save(entry));
    }

    @Transactional(readOnly = true)
    public List<EntryResponse> getEntries(Long semesterId, Long timetableId) {
        timetableService.getTimetableEntity(semesterId, timetableId);
        return entryRepository.findAllByTimetable_IdOrderByIdAsc(timetableId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void deleteEntry(Long semesterId, Long timetableId, Long entryId) {
        timetableService.getTimetableEntity(semesterId, timetableId);
        TimetableEntry entry = entryRepository.findByIdAndTimetable_Id(entryId, timetableId)
                .orElseThrow(() -> new RuntimeException("시간표 배치를 찾을 수 없습니다. ID: " + entryId));
        entryRepository.delete(entry);
    }

    private EntryResponse toResponse(TimetableEntry entry) {
        Course course = entry.getCourse();
        return new EntryResponse(
                entry.getId(),
                entry.getTimetable().getId(),
                course.getId(),
                course.getTitle(),
                course.getClassroom(),
                course.getProfessor(),
                entry.getDayOfWeek(),
                entry.getStartTime(),
                entry.getEndTime(),
                entry.getCreatedAt(),
                entry.getUpdatedAt()
        );
    }
}
