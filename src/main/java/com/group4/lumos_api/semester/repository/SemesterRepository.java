package com.group4.lumos_api.semester.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group4.lumos_api.semester.entity.Semester;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {

    List<Semester> findAllByUser_UserIdOrderBySortOrderAscIdAsc(String userId);
    Optional<Semester> findByIdAndUser_UserId(Long id, String userId);
}
