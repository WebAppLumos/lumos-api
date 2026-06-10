package com.group4.lumos_api.timetable.repository;

import com.group4.lumos_api.timetable.entity.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {

    List<Timetable> findAllBySemester_IdOrderByIdAsc(Long semesterId);

    Optional<Timetable> findByIdAndSemester_User_Id(Long timetableId, String userId);
}
