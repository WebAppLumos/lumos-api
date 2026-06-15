package com.group4.lumos_api.recent_semester_credits.service;

import com.group4.lumos_api.course.entity.Course;
import com.group4.lumos_api.course.repository.CourseRepository;
import com.group4.lumos_api.semester.entity.Semester;
import com.group4.lumos_api.semester.repository.SemesterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecentSemesterCreditsService {

    private final SemesterRepository semesterRepository;
    private final CourseRepository courseRepository;

    /**
     * 현재 학기(활성 학기, 없으면 sortOrder 기준 마지막)의 모든 수업 학점 합계를 반환한다.
     */
    public Integer getRecentSemesterTotalCredits(String userId) {
        List<Semester> semesters = semesterRepository.findAllByUser_UserIdOrderBySortOrderAscIdAsc(userId);

        if (semesters.isEmpty()) {
            return 0;
        }

        Semester currentSemester = semesters.stream()
                .filter(semester -> Boolean.TRUE.equals(semester.getIsActive()))
                .findFirst()
                .orElse(semesters.get(semesters.size() - 1));

        List<Course> courses = courseRepository.findAllBySemester_IdOrderByIdAsc(currentSemester.getId());

        return courses.stream()
                .mapToInt(course -> course.getCredit() != null ? course.getCredit().intValue() : 0)
                .sum();
    }
}
