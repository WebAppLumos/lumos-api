package com.group4.lumos_api.entry.service;

import com.group4.lumos_api.common.exception.BadRequestException;
import com.group4.lumos_api.common.exception.ConflictException;
import com.group4.lumos_api.common.exception.NotFoundException;
import com.group4.lumos_api.course.entity.Course;
import com.group4.lumos_api.course.repository.CourseRepository;
import com.group4.lumos_api.entry.dto.EntryCreateRequest;
import com.group4.lumos_api.entry.dto.EntryResponse;
import com.group4.lumos_api.entry.dto.EntryUpdateRequest;
import com.group4.lumos_api.entry.entity.TimetableEntry;
import com.group4.lumos_api.entry.repository.TimetableEntryRepository;
import com.group4.lumos_api.note.repository.NoteRepository;
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
    private final NoteRepository noteRepository;
    private final TimetableService timetableService;

    public EntryResponse createEntry(String userId, Long timetableId, EntryCreateRequest request) {
        Timetable timetable = timetableService.getOwnedTimetableEntity(userId, timetableId);
        Course course = courseRepository.findByIdAndSemester_User_UserId(request.getCourseId(), userId)
                .orElseThrow(() -> new NotFoundException("수업을 찾을 수 없습니다. ID: " + request.getCourseId()));

        if (!course.getSemester().getId().equals(timetable.getSemester().getId())) {
            throw new BadRequestException("시간표와 다른 학기의 수업은 배치할 수 없습니다. course ID: " + request.getCourseId());
        }

        if (entryRepository.existsByTimetable_IdAndCourse_Id(timetableId, request.getCourseId())) {
            throw new ConflictException("이미 시간표에 배치된 수업입니다. ID: " + request.getCourseId());
        }

        validateTimeRange(request.getStartTime(), request.getEndTime());
        validateNoConflict(timetableId, request.getDayOfWeek(), request.getStartTime(), request.getEndTime(), null);

        TimetableEntry entry = new TimetableEntry();
        entry.setTimetable(timetable);
        entry.setCourse(course);
        entry.setDayOfWeek(request.getDayOfWeek());
        entry.setStartTime(request.getStartTime());
        entry.setEndTime(request.getEndTime());
        return toResponse(entryRepository.save(entry));
    }

    public EntryResponse updateEntry(String userId, Long entryId, EntryUpdateRequest request) {
        TimetableEntry entry = getOwnedEntry(userId, entryId);

        validateTimeRange(request.getStartTime(), request.getEndTime());
        validateNoConflict(entry.getTimetable().getId(), request.getDayOfWeek(), request.getStartTime(), request.getEndTime(), entryId);

        entry.setDayOfWeek(request.getDayOfWeek());
        entry.setStartTime(request.getStartTime());
        entry.setEndTime(request.getEndTime());
        return toResponse(entryRepository.save(entry));
    }

    @Transactional(readOnly = true)
    public List<EntryResponse> getEntries(String userId, Long timetableId) {
        timetableService.getOwnedTimetableEntity(userId, timetableId);
        return entryRepository.findAllByTimetable_IdOrderByIdAsc(timetableId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void deleteEntry(String userId, Long entryId) {
        TimetableEntry entry = getOwnedEntry(userId, entryId);
        Long courseId = entry.getCourse().getId();
        entryRepository.delete(entry);
        if (!entryRepository.existsByCourse_Id(courseId)) {
            noteRepository.deleteAllByCourse_Id(courseId);
        }
    }

    /**
     * 현재 사용자가 소유한 시간표 배치를 반환한다. 없거나 타인 소유면 404.
     */
    private TimetableEntry getOwnedEntry(String userId, Long entryId) {
        return entryRepository.findByIdAndTimetable_Semester_User_UserId(entryId, userId)
                .orElseThrow(() -> new NotFoundException("시간표 배치를 찾을 수 없습니다. ID: " + entryId));
    }

    private void validateTimeRange(java.time.LocalTime startTime, java.time.LocalTime endTime) {
        if (!startTime.isBefore(endTime)) {
            throw new BadRequestException("시작 시간은 종료 시간보다 앞서야 합니다.");
        }
    }

    private void validateNoConflict(Long timetableId, Short dayOfWeek,
                                    java.time.LocalTime startTime, java.time.LocalTime endTime,
                                    Long excludeEntryId) {
        boolean overlaps = entryRepository.findAllByTimetable_IdAndDayOfWeek(timetableId, dayOfWeek).stream()
                .filter(existing -> !existing.getId().equals(excludeEntryId))
                .anyMatch(existing -> startTime.isBefore(existing.getEndTime())
                        && existing.getStartTime().isBefore(endTime));
        if (overlaps) {
            throw new ConflictException("같은 요일에 시간이 겹치는 수업이 이미 배치되어 있습니다.");
        }
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
