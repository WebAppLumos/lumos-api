package com.group4.lumos_api.course.repository;

import com.group4.lumos_api.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findAllBySemester_IdOrderByIdAsc(Long semesterId);

    Optional<Course> findByIdAndSemester_User_UserId(Long courseId, String userId);
}
