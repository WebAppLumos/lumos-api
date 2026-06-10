package com.group4.lumos_api.note.service;

import com.group4.lumos_api.common.exception.NotFoundException;
import com.group4.lumos_api.course.entity.Course;
import com.group4.lumos_api.course.repository.CourseRepository;
import com.group4.lumos_api.note.dto.NotePinRequest;
import com.group4.lumos_api.note.dto.NoteRequest;
import com.group4.lumos_api.note.dto.NoteResponse;
import com.group4.lumos_api.note.entity.Note;
import com.group4.lumos_api.note.repository.NoteRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NoteService {

    private final NoteRepository noteRepository;
    private final CourseRepository courseRepository;

    public NoteResponse createNote(String userId, Long courseId, NoteRequest request) {
        Course course = getOwnedCourse(userId, courseId);

        Note note = new Note();
        note.setCourse(course);
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());

        return toResponse(noteRepository.save(note));
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> getNotes(String userId, Long courseId, String keyword) {
        getOwnedCourse(userId, courseId);
        List<Note> notes = keyword == null || keyword.isBlank()
                ? noteRepository.findAllByCourse_IdOrderByIsPinnedDescUpdatedAtDesc(courseId)
                : noteRepository.findAllByCourse_IdAndTitleContainingIgnoreCaseOrderByIsPinnedDescUpdatedAtDesc(courseId, keyword);
        return notes.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public NoteResponse getNote(String userId, Long noteId) {
        return toResponse(getOwnedNote(userId, noteId));
    }

    public NoteResponse updateNote(String userId, Long noteId, NoteRequest request) {
        Note note = getOwnedNote(userId, noteId);
        if (request.getTitle() != null) {
            note.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            note.setContent(request.getContent());
        }
        return toResponse(noteRepository.save(note));
    }

    public void deleteNote(String userId, Long noteId) {
        noteRepository.delete(getOwnedNote(userId, noteId));
    }

    public NoteResponse setPinned(String userId, Long noteId, NotePinRequest request) {
        Note note = getOwnedNote(userId, noteId);
        note.setIsPinned(request.getIsPinned() != null ? request.getIsPinned() : !note.getIsPinned());
        return toResponse(noteRepository.save(note));
    }

    private Course getOwnedCourse(String userId, Long courseId) {
        return courseRepository.findByIdAndSemester_User_UserId(courseId, userId)
                .orElseThrow(() -> new NotFoundException("수업을 찾을 수 없습니다. ID: " + courseId));
    }

    private Note getOwnedNote(String userId, Long noteId) {
        return noteRepository.findByIdAndCourse_Semester_User_UserId(noteId, userId)
                .orElseThrow(() -> new NotFoundException("노트를 찾을 수 없습니다. ID: " + noteId));
    }

    private NoteResponse toResponse(Note note) {
        return new NoteResponse(
                note.getId(),
                note.getCourse().getId(),
                note.getTitle(),
                note.getContent(),
                note.getIsPinned(),
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }
}
