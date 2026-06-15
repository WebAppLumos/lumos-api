package com.group4.lumos_api.assignment.repository;

import com.group4.lumos_api.assignment.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findAllByUserId(String userId);
    boolean existsByUserIdAndTitleAndCourse(String userId, String title, String course);
    Optional<Assignment> findByUserIdAndCourseAndTitle(String userId, String course, String title);
}