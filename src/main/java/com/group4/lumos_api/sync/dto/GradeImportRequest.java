package com.group4.lumos_api.sync.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GradeImportRequest {

    @NotBlank
    private String ssv;

    @NotBlank
    @Pattern(regexp = "\\d{7}")
    private String studentNumber;
}
