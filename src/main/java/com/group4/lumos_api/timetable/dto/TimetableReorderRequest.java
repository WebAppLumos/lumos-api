package com.group4.lumos_api.timetable.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class TimetableReorderRequest {
    @NotEmpty
    private List<Long> timetableIds;
}
