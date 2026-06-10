package com.group4.lumos_api.timetable.service;

import com.group4.lumos_api.common.exception.NotFoundException;
import com.group4.lumos_api.entry.repository.TimetableEntryRepository;
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

@Service
@RequiredArgsConstructor
@Transactional
public class TimetableService {

    private final TimetableRepository timetableRepository;
    private final SemesterRepository semesterRepository;
    private final TimetableEntryRepository entryRepository;

    public TimetableResponse createTimetable(String userId, Long semesterId, TimetableRequest request) {
        Semester semester = getOwnedSemester(userId, semesterId);

        Timetable timetable = new Timetable();
        timetable.setSemester(semester);
        timetable.setTitle(request.getTitle());

        return toResponse(timetableRepository.save(timetable));
    }

    @Transactional(readOnly = true)
    public List<TimetableResponse> getTimetables(String userId, Long semesterId) {
        getOwnedSemester(userId, semesterId);
        return timetableRepository.findAllBySemester_IdOrderByIdAsc(semesterId)
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
        Timetable timetable = getOwnedTimetableEntity(userId, timetableId);
        // 시간표에 배치된 수업(Entry)을 먼저 제거한 뒤 시간표를 삭제한다.
        entryRepository.deleteAllByTimetable_Id(timetableId);
        timetableRepository.delete(timetable);
    }

    /**
     * 현재 사용자가 소유한 시간표 엔티티를 반환한다. 없거나 타인 소유면 404.
     * (Entry 도메인에서 소유권 검증용으로 재사용)
     */
    public Timetable getOwnedTimetableEntity(String userId, Long timetableId) {
        return timetableRepository.findByIdAndSemester_User_Id(timetableId, userId)
                .orElseThrow(() -> new NotFoundException("시간표를 찾을 수 없습니다. ID: " + timetableId));
    }

    private Semester getOwnedSemester(String userId, Long semesterId) {
        return semesterRepository.findByIdAndUser_Id(semesterId, userId)
                .orElseThrow(() -> new NotFoundException("학기를 찾을 수 없습니다. ID: " + semesterId));
    }

    private TimetableResponse toResponse(Timetable timetable) {
        return new TimetableResponse(
                timetable.getId(),
                timetable.getSemester().getId(),
                timetable.getTitle(),
                timetable.getCreatedAt(),
                timetable.getUpdatedAt()
        );
    }
}
