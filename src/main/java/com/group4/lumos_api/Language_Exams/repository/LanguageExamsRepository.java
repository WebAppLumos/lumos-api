package com.group4.lumos_api.Language_Exams.repository;

import com.group4.lumos_api.Language_Exams.entity.LanguageExams;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LanguageExamsRepository extends JpaRepository<LanguageExams, Long> {
    List<LanguageExams> findByStudentStudentID(Long studentId);
}
