package com.group4.lumos_api.recent_semester_credits.service;

import com.group4.lumos_api.course.entity.Course;
import com.group4.lumos_api.course.repository.CourseRepository;
import com.group4.lumos_api.semester.entity.Semester;
import com.group4.lumos_api.semester.repository.SemesterRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecentSemesterCreditsServiceTest {

    @Mock
    private SemesterRepository semesterRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private RecentSemesterCreditsService recentSemesterCreditsService;

    @Test
    @DisplayName("학기가 없을 경우 0학점을 반환한다")
    void getRecentSemesterTotalCredits_NoSemesters() {
        String userId = "test-user";
        when(semesterRepository.findAllByUser_UserIdOrderBySortOrderAscIdAsc(userId))
                .thenReturn(Collections.emptyList());

        Integer result = recentSemesterCreditsService.getRecentSemesterTotalCredits(userId);

        assertEquals(0, result);
    }

    @Test
    @DisplayName("가장 최근 학기(시작일 기준)의 학점 합계를 올바르게 계산한다")
    void getRecentSemesterTotalCredits_Success() {
        String userId = "test-user";

        // 학기 1 (과거)
        Semester s1 = new Semester();
        s1.setId(1L);
        s1.setStartDate(LocalDate.of(2023, 3, 1));

        // 학기 2 (최근)
        Semester s2 = new Semester();
        s2.setId(2L);
        s2.setStartDate(LocalDate.of(2024, 3, 1));

        when(semesterRepository.findAllByUser_UserIdOrderBySortOrderAscIdAsc(userId))
                .thenReturn(Arrays.asList(s1, s2));

        // 학기 2의 수업들
        Course c1 = new Course();
        c1.setCredit((short) 3);
        Course c2 = new Course();
        c2.setCredit((short) 2);
        Course c3 = new Course();
        c3.setCredit(null); // null 체크 확인용

        when(courseRepository.findAllBySemester_IdOrderByIdAsc(2L))
                .thenReturn(Arrays.asList(c1, c2, c3));

        Integer result = recentSemesterCreditsService.getRecentSemesterTotalCredits(userId);

        assertEquals(5, result);
    }
}
