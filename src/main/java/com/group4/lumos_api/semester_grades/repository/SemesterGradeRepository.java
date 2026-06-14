package com.group4.lumos_api.semester_grades.repository;

import com.group4.lumos_api.semester_grades.entity.SemesterGrade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SemesterGradeRepository extends JpaRepository<SemesterGrade, Long> {

    List<SemesterGrade> findAllByUser_UserIdOrderByAcademicYearAscTermCodeAsc(String userId);

    Optional<SemesterGrade> findByUser_UserIdAndAcademicYearAndTermCode(
            String userId, Integer academicYear, String termCode);

    void deleteAllByUser_UserId(String userId);
}
