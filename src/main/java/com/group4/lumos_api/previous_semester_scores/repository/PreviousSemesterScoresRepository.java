package com.group4.lumos_api.previous_semester_scores.repository;

import com.group4.lumos_api.previous_semester_scores.entity.PreviousSemesterScores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreviousSemesterScoresRepository extends JpaRepository<PreviousSemesterScores, Long> {
    List<PreviousSemesterScores> findByUserUserId(String userId);
}
