package com.group4.lumos_api.language_exams.repository;

import com.group4.lumos_api.language_exams.entity.LanguageExams;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LanguageExamsRepository extends JpaRepository<LanguageExams, Long> {
    List<LanguageExams> findByUserUserId(String userId);
}
