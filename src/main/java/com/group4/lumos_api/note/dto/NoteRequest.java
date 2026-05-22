package com.group4.lumos_api.note.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NoteRequest {

    @Size(max = 100)
    private String title;

    private String content;
}
