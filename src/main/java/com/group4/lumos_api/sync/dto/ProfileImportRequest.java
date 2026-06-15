package com.group4.lumos_api.sync.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileImportRequest {

    @NotBlank
    @Pattern(regexp = "\\d{7}")
    private String studentNumber;

    @NotBlank
    private String major;

    @NotNull
    @Min(1)
    @Max(8)
    private Integer grade;
}
