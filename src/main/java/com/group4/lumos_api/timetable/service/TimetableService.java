package com.group4.lumos_api.timetable.service;

import com.group4.lumos_api.common.exception.BadRequestException;
import com.group4.lumos_api.common.exception.NotFoundException;
import com.group4.lumos_api.entry.repository.TimetableEntryRepository;
import com.group4.lumos_api.note.repository.NoteRepository;
import com.group4.lumos_api.semester.entity.Semester;
import com.group4.lumos_api.semester.repository.SemesterRepository;
import com.group4.lumos_api.timetable.dto.TimetableRequest;
import com.group4.lumos_api.timetable.dto.TimetableResponse;
import com.group4.lumos_api.timetable.entity.Timetable;
import com.group4.lumos_api.timetable.repository.TimetableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.group4.lumos_api.entry.entity.TimetableEntry;

@Service
@RequiredArgsConstructor
@Transactional
public class TimetableService {

    private final TimetableRepository timetableRepository;
    private final SemesterRepository semesterRepository;
    private final TimetableEntryRepository entryRepository;
    private final NoteRepository noteRepository;

    public TimetableResponse createTimetable(String userId, Long semesterId, TimetableRequest request) {
        Semester semester = getOwnedSemester(userId, semesterId);

        Timetable timetable = new Timetable();
        timetable.setSemester(semester);
        timetable.setTitle(request.getTitle());
        timetable.setSortOrder(nextSortOrder(semesterId));

        return toResponse(timetableRepository.save(timetable));
    }

    public List<TimetableResponse> reorderTimetables(String userId, Long semesterId, List<Long> timetableIds) {
        getOwnedSemester(userId, semesterId);
        List<Timetable> timetables = timetableRepository.findAllBySemester_IdOrderBySortOrderAscIdAsc(semesterId);
        Set<Long> ownedIds = timetables.stream().map(Timetable::getId).collect(Collectors.toSet());

        if (timetableIds.size() != ownedIds.size() || !ownedIds.containsAll(timetableIds)) {
            throw new BadRequestException("시간표 순서 변경 요청이 올바르지 않습니다.");
        }

        Map<Long, Timetable> byId = timetables.stream()
                .collect(Collectors.toMap(Timetable::getId, Function.identity()));

        for (int i = 0; i < timetableIds.size(); i++) {
            byId.get(timetableIds.get(i)).setSortOrder(i);
        }

        return timetableRepository.findAllBySemester_IdOrderBySortOrderAscIdAsc(semesterId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TimetableResponse> getTimetables(String userId, Long semesterId) {
        getOwnedSemester(userId, semesterId);
        return timetableRepository.findAllBySemester_IdOrderBySortOrderAscIdAsc(semesterId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TimetableResponse getTimetable(String userId, Long timetableId) {
        return toResponse(getOwnedTimetableEntity(userId, timetableId));
    }

    public TimetableResponse updateTimetable(String userId, Long timetableId, TimetableRequest request) {
        Timetable timetable = getOwnedTimetableEntity(userId, timetableId);
        if (request.getTitle() != null) {
            timetable.setTitle(request.getTitle());
        }
        return toResponse(timetableRepository.save(timetable));
    }

    public void deleteTimetable(String userId, Long timetableId) {
        getOwnedTimetableEntity(userId, timetableId);
        List<TimetableEntry> entries = entryRepository.findAllByTimetable_IdOrderByIdAsc(timetableId);
        Set<Long> courseIds = entries.stream()
                .map(entry -> entry.getCourse().getId())
                .collect(Collectors.toSet());

        entryRepository.deleteAllByTimetable_Id(timetableId);
        timetableRepository.deleteById(timetableId);

        courseIds.forEach(this::deleteNotesIfCourseHasNoEntries);
    }

    private void deleteNotesIfCourseHasNoEntries(Long courseId) {
        if (!entryRepository.existsByCourse_Id(courseId)) {
            noteRepository.deleteAllByCourse_Id(courseId);
        }
    }

    /**
     * 현재 사용자가 소유한 시간표 엔티티를 반환한다. 없거나 타인 소유면 404.
     * (Entry 도메인에서 소유권 검증용으로 재사용)
     */
    public Timetable getOwnedTimetableEntity(String userId, Long timetableId) {
        return timetableRepository.findByIdAndSemester_User_UserId(timetableId, userId)
                .orElseThrow(() -> new NotFoundException("시간표를 찾을 수 없습니다. ID: " + timetableId));
    }

    private Semester getOwnedSemester(String userId, Long semesterId) {
        return semesterRepository.findByIdAndUser_UserId(semesterId, userId)
                .orElseThrow(() -> new NotFoundException("학기를 찾을 수 없습니다. ID: " + semesterId));
    }

    private int nextSortOrder(Long semesterId) {
        return timetableRepository.findAllBySemester_IdOrderBySortOrderAscIdAsc(semesterId)
                .stream()
                .mapToInt(Timetable::getSortOrder)
                .max()
                .orElse(-1) + 1;
    }

    private TimetableResponse toResponse(Timetable timetable) {
        return new TimetableResponse(
                timetable.getId(),
                timetable.getSemester().getId(),
                timetable.getTitle(),
                timetable.getSortOrder(),
                timetable.getCreatedAt(),
                timetable.getUpdatedAt()
        );
    }
}
