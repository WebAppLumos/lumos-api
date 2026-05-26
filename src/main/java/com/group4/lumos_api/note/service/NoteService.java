package com.group4.lumos_api.note.service;

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

    public NoteResponse createNote(Long semesterId, Long courseId, NoteRequest request) {
        Course course = getCourse(semesterId, courseId);

        Note note = new Note();
        note.setCourse(course);
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());

        return toResponse(noteRepository.save(note));
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> getNotes(Long semesterId, Long courseId, String keyword) {
        getCourse(semesterId, courseId);
        List<Note> notes = keyword == null || keyword.isBlank()
                ? noteRepository.findAllByCourse_IdOrderByIsPinnedDescUpdatedAtDesc(courseId)
                : noteRepository.findAllByCourse_IdAndTitleContainingIgnoreCaseOrderByIsPinnedDescUpdatedAtDesc(courseId, keyword);
        return notes.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public NoteResponse getNote(Long semesterId, Long courseId, Long noteId) {
        getCourse(semesterId, courseId);
        return toResponse(getNoteEntity(courseId, noteId));
    }

    public NoteResponse updateNote(Long semesterId, Long courseId, Long noteId, NoteRequest request) {
        getCourse(semesterId, courseId);
        Note note = getNoteEntity(courseId, noteId);
        if (request.getTitle() != null) {
            note.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            note.setContent(request.getContent());
        }
        return toResponse(noteRepository.save(note));
    }

    public void deleteNote(Long semesterId, Long courseId, Long noteId) {
        getCourse(semesterId, courseId);
        noteRepository.delete(getNoteEntity(courseId, noteId));
    }

    public NoteResponse setPinned(Long semesterId, Long courseId, Long noteId, NotePinRequest request) {
        getCourse(semesterId, courseId);
        Note note = getNoteEntity(courseId, noteId);
        note.setIsPinned(request.getIsPinned() != null ? request.getIsPinned() : !note.getIsPinned());
        return toResponse(noteRepository.save(note));
    }

    private Course getCourse(Long semesterId, Long courseId) {
        return courseRepository.findByIdAndSemester_Id(courseId, semesterId)
                .orElseThrow(() -> new RuntimeException("수업을 찾을 수 없습니다. ID: " + courseId));
    }

    private Note getNoteEntity(Long courseId, Long noteId) {
        return noteRepository.findByIdAndCourse_Id(noteId, courseId)
                .orElseThrow(() -> new RuntimeException("노트를 찾을 수 없습니다. ID: " + noteId));
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
