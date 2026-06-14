package com.group4.lumos_api.assignment.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignments", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "course", "title"}))
@Getter @Setter
@NoArgsConstructor 
@AllArgsConstructor
@Builder
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false, length = 100)
    private String course;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted;
}
