package com.group4.lumos_api.note.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group4.lumos_api.note.entity.Note;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findAllByCourse_IdOrderByIsPinnedDescUpdatedAtDesc(Long courseId);

    List<Note> findAllByCourse_IdAndTitleContainingIgnoreCaseOrderByIsPinnedDescUpdatedAtDesc(Long courseId, String title);

    Optional<Note> findByIdAndCourse_Semester_User_Id(Long noteId, String userId);

    void deleteAllByCourse_Id(Long courseId);
}
