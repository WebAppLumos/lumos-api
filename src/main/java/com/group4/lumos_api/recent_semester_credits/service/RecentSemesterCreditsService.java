package com.group4.lumos_api.recent_semester_credits.service;

import com.group4.lumos_api.course.entity.Course;
import com.group4.lumos_api.course.repository.CourseRepository;
import com.group4.lumos_api.semester.entity.Semester;
import com.group4.lumos_api.semester.repository.SemesterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecentSemesterCreditsService {

    private final SemesterRepository semesterRepository;
    private final CourseRepository courseRepository;

    /**
     * 가장 최근 학기(시작일 기준)의 모든 수업 학점 합계를 반환한다.
     */
    public Integer getRecentSemesterTotalCredits(String userId) {
        // 사용자의 모든 학기를 가져온다. (기존 메서드 활용)
        List<Semester> semesters = semesterRepository.findAllByUser_UserIdOrderBySortOrderAscIdAsc(userId);

        if (semesters.isEmpty()) {
            return 0;
        }

        // 시작일(startDate)이 가장 늦은 학기를 찾는다.
        Semester recentSemester = semesters.stream()
                .max(Comparator.comparing(Semester::getStartDate))
                .orElse(null);

        if (recentSemester == null) {
            return 0;
        }

        // 해당 학기의 모든 수업을 가져온다.
        List<Course> courses = courseRepository.findAllBySemester_IdOrderByIdAsc(recentSemester.getId());

        // 학점 합계를 계산한다. (null인 경우 0으로 처리)
        return courses.stream()
                .mapToInt(course -> course.getCredit() != null ? course.getCredit().intValue() : 0)
                .sum();
    }
}
