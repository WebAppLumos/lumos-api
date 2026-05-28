package com.group4.lumos_api.timetable.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TimetableRequest {

    @Size(max = 100)
    private String title;
}
