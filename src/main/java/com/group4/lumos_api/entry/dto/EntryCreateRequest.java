package com.group4.lumos_api.entry.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class EntryCreateRequest {

    @NotNull
    private Long courseId;

    private Short dayOfWeek;

    private LocalTime startTime;

    private LocalTime endTime;
}
