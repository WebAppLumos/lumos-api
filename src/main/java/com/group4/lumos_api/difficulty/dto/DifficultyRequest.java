package com.group4.lumos_api.difficulty.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DifficultyRequest {

    @NotNull
    @Min(1)
    @Max(5)
    private Short level;
}
