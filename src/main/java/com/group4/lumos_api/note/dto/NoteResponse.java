package com.group4.lumos_api.note.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class NoteResponse {
    private Long id;
    private Long courseId;
    private String title;
    private String content;
    private Boolean isPinned;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
