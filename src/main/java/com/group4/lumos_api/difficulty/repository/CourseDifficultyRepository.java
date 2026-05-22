package com.group4.lumos_api.difficulty.repository;

import com.group4.lumos_api.difficulty.entity.CourseDifficulty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseDifficultyRepository extends JpaRepository<CourseDifficulty, Long> {

    Optional<CourseDifficulty> findByCourse_Id(Long courseId);

    List<CourseDifficulty> findAllByCourse_IdIn(Collection<Long> courseIds);
}
