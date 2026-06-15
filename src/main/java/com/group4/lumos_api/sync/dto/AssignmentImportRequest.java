package com.group4.lumos_api.sync.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AssignmentImportRequest {

    @NotEmpty
    @Valid
    private List<AssignmentImportItem> assignments;
}
