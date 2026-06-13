package com.group4.lumos_api.semester.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class SemesterReorderRequest {
    @NotEmpty
    private List<Long> semesterIds;
}
