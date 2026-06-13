package com.group4.lumos_api.assignment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AssignmentRequest {
    private String course;
    private String title;
    private String deadline;
    private Boolean isCompleted;
}