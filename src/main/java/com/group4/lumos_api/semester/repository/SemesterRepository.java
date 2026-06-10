package com.group4.lumos_api.semester.repository;

import com.group4.lumos_api.semester.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {

    List<Semester> findAllByUser_IdOrderByIdAsc(String userId);

    Optional<Semester> findByIdAndUser_Id(Long id, String userId);
}
