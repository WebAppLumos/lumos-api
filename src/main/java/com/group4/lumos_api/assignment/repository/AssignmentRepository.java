package com.group4.lumos_api.assignment.repository;

import com.group4.lumos_api.assignment.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    boolean existsByTitleAndCourse(String title, String course);
}