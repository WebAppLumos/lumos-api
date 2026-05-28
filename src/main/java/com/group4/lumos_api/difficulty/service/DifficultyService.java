package com.group4.lumos_api.difficulty.service;

import com.group4.lumos_api.course.entity.Course;
import com.group4.lumos_api.course.repository.CourseRepository;
import com.group4.lumos_api.difficulty.dto.DifficultyRequest;
import com.group4.lumos_api.difficulty.dto.DifficultyResponse;
import com.group4.lumos_api.difficulty.dto.TimetableDifficultyResponse;
import com.group4.lumos_api.difficulty.entity.CourseDifficulty;
import com.group4.lumos_api.difficulty.repository.CourseDifficultyRepository;
import com.group4.lumos_api.entry.entity.TimetableEntry;
import com.group4.lumos_api.entry.repository.TimetableEntryRepository;
import com.group4.lumos_api.timetable.service.TimetableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DifficultyService {

    private final CourseDifficultyRepository difficultyRepository;
    private final CourseRepository courseRepository;
    private final TimetableEntryRepository entryRepository;
    private final TimetableService timetableService;

    public DifficultyResponse setDifficulty(Long semesterId, Long courseId, DifficultyRequest request) {
        Course course = getCourse(semesterId, courseId);
        CourseDifficulty difficulty = difficultyRepository.findByCourse_Id(courseId)
                .orElseGet(CourseDifficulty::new);

        difficulty.setCourse(course);
        difficulty.setLevel(request.getLevel());

        return toResponse(difficultyRepository.save(difficulty));
    }

    @Transactional(readOnly = true)
    public DifficultyResponse getDifficulty(Long semesterId, Long courseId) {
        getCourse(semesterId, courseId);
        CourseDifficulty difficulty = difficultyRepository.findByCourse_Id(courseId)
                .orElseThrow(() -> new RuntimeException("난이도를 찾을 수 없습니다. course ID: " + courseId));
        return toResponse(difficulty);
    }

    @Transactional(readOnly = true)
    public TimetableDifficultyResponse getTimetableAverage(Long semesterId, Long timetableId) {
        timetableService.getTimetableEntity(semesterId, timetableId);
        List<TimetableEntry> entries = entryRepository.findAllByTimetable_IdOrderByIdAsc(timetableId);
        List<Long> courseIds = entries.stream().map(entry -> entry.getCourse().getId()).toList();
        List<CourseDifficulty> difficulties = difficultyRepository.findAllByCourse_IdIn(courseIds);

        double average = difficulties.stream()
                .mapToInt(CourseDifficulty::getLevel)
                .average()
                .orElse(0.0);

        return new TimetableDifficultyResponse(
                timetableId,
                entries.size(),
                difficulties.size(),
                average
        );
    }

    private Course getCourse(Long semesterId, Long courseId) {
        return courseRepository.findByIdAndSemester_Id(courseId, semesterId)
                .orElseThrow(() -> new RuntimeException("수업을 찾을 수 없습니다. ID: " + courseId));
    }

    private DifficultyResponse toResponse(CourseDifficulty difficulty) {
        return new DifficultyResponse(
                difficulty.getId(),
                difficulty.getCourse().getId(),
                difficulty.getLevel(),
                difficulty.getCreatedAt(),
                difficulty.getUpdatedAt()
        );
    }
}
