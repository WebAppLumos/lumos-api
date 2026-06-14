package com.group4.lumos_api.assignment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.group4.lumos_api.assignment.entity.Assignment;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentResponse {
    private Long id;
    private String course;
    private String title;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime deadline;
    
    private Boolean isCompleted;

    public static AssignmentResponse from(Assignment assignment) {
        return AssignmentResponse.builder()
                .id(assignment.getId())
                .course(assignment.getCourse())
                .title(assignment.getTitle())
                .deadline(assignment.getDeadline())
                .isCompleted(assignment.isCompleted())
                .build();
    }
}
