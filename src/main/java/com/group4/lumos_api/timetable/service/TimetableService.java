package com.group4.lumos_api.timetable.service;

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

    public TimetableResponse createTimetable(Long semesterId, TimetableRequest request) {
        Semester semester = getSemester(semesterId);

        Timetable timetable = new Timetable();
        timetable.setSemester(semester);
        timetable.setTitle(request.getTitle());

        return toResponse(timetableRepository.save(timetable));
    }

    @Transactional(readOnly = true)
    public List<TimetableResponse> getTimetables(Long semesterId) {
        getSemester(semesterId);
        return timetableRepository.findAllBySemester_IdOrderByIdAsc(semesterId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TimetableResponse getTimetable(Long semesterId, Long timetableId) {
        return toResponse(getTimetableEntity(semesterId, timetableId));
    }

    public TimetableResponse updateTimetable(Long semesterId, Long timetableId, TimetableRequest request) {
        Timetable timetable = getTimetableEntity(semesterId, timetableId);
        if (request.getTitle() != null) {
            timetable.setTitle(request.getTitle());
        }
        return toResponse(timetableRepository.save(timetable));
    }

    public void deleteTimetable(Long semesterId, Long timetableId) {
        timetableRepository.delete(getTimetableEntity(semesterId, timetableId));
    }

    public Timetable getTimetableEntity(Long semesterId, Long timetableId) {
        return timetableRepository.findByIdAndSemester_Id(timetableId, semesterId)
                .orElseThrow(() -> new RuntimeException("시간표를 찾을 수 없습니다. ID: " + timetableId));
    }

    private Semester getSemester(Long semesterId) {
        return semesterRepository.findById(semesterId)
                .orElseThrow(() -> new RuntimeException("학기를 찾을 수 없습니다. ID: " + semesterId));
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
