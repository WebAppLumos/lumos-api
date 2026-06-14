package com.group4.lumos_api.semester_grades.service;

import com.group4.lumos_api.semester_grades.dto.SemesterGradeItemDto;
import com.group4.lumos_api.semester_grades.dto.SemesterGradeSummaryDto;
import com.group4.lumos_api.semester_grades.entity.SemesterGrade;
import com.group4.lumos_api.semester_grades.repository.SemesterGradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SemesterGradeService {

    private static final Map<String, String> TERM_LABEL_SUFFIX = Map.of(
            "1", "1",
            "2", "2",
            "3", "하",
            "4", "동"
    );

    private final SemesterGradeRepository semesterGradeRepository;

    public SemesterGradeSummaryDto getSummaryForUser(String userId) {
        List<SemesterGrade> grades = semesterGradeRepository
                .findAllByUser_UserIdOrderByAcademicYearAscTermCodeAsc(userId)
                .stream()
                .sorted(Comparator
                        .comparingInt(SemesterGrade::getAcademicYear)
                        .thenComparingInt(this::termSortOrder))
                .toList();

        List<SemesterGradeItemDto> items = grades.stream()
                .map(this::toItemDto)
                .toList();

        int totalCompletedCredits = items.stream()
                .mapToInt(SemesterGradeItemDto::getCompletedCredits)
                .sum();

        double weightedGpaSum = 0.0;
        int weightedCreditSum = 0;
        for (SemesterGradeItemDto item : items) {
            if (item.getCompletedCredits() > 0) {
                weightedGpaSum += item.getGpa() * item.getCompletedCredits();
                weightedCreditSum += item.getCompletedCredits();
            }
        }

        double averageGpa = weightedCreditSum > 0
                ? weightedGpaSum / weightedCreditSum
                : 0.0;

        int academicWarningCount = (int) items.stream()
                .filter(SemesterGradeItemDto::getAcademicWarning)
                .count();

        return SemesterGradeSummaryDto.builder()
                .totalCompletedCredits(totalCompletedCredits)
                .averageGpa(averageGpa)
                .academicWarningCount(academicWarningCount)
                .lastSyncedAt(grades.stream()
                        .map(SemesterGrade::getSyncedAt)
                        .max(Comparator.naturalOrder())
                        .orElse(null))
                .semesters(items)
                .build();
    }

    private SemesterGradeItemDto toItemDto(SemesterGrade grade) {
        String yearSuffix = String.valueOf(grade.getAcademicYear()).substring(2);
        String termSuffix = TERM_LABEL_SUFFIX.getOrDefault(grade.getTermCode(), grade.getTermCode());

        return SemesterGradeItemDto.builder()
                .semesterGradeId(grade.getSemesterGradeId())
                .academicYear(grade.getAcademicYear())
                .termCode(grade.getTermCode())
                .termName(grade.getTermName())
                .label(yearSuffix + "-" + termSuffix)
                .completedCredits(grade.getCompletedCredits())
                .registeredCredits(grade.getRegisteredCredits())
                .gpa(grade.getGpa())
                .academicWarning(grade.getAcademicWarning())
                .syncedAt(grade.getSyncedAt())
                .build();
    }

    private int termSortOrder(SemesterGrade grade) {
        return switch (grade.getTermCode()) {
            case "1" -> 1;
            case "3" -> 2;
            case "2" -> 3;
            case "4" -> 4;
            default -> 99;
        };
    }
}
