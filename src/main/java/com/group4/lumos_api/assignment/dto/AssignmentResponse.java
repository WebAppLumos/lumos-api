package com.group4.lumos_api.assignment.dto;

import com.group4.lumos_api.assignment.entity.Assignment;
import lombok.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentResponse {
    private Long id;
    private String course;
    private String title;
    private String deadline;
    private Boolean isCompleted;
    private String statusClass;

    public static AssignmentResponse from(Assignment assignment) {
        return AssignmentResponse.builder()
                .id(assignment.getId())
                .course(assignment.getCourse())
                .title(assignment.getTitle())
                .deadline(assignment.getDeadline())
                .isCompleted(assignment.isCompleted())
                .statusClass(calculateStatusClass(assignment.getDeadline()))
                .build();
    }

    private static String calculateStatusClass(String deadlineStr) {
        try {
            LocalDate today = LocalDate.now();
            LocalDate deadline = LocalDate.parse(deadlineStr);
            long daysLeft = ChronoUnit.DAYS.between(today, deadline);
            if (daysLeft <= 1) return "d-day-urgent";
            else if (daysLeft <= 3) return "d-day-warning";
            else return "d-day-info";
        } catch (Exception e) {
            return "d-day-info";
        }
    }
}