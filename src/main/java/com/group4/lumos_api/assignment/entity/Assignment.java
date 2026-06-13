package com.group4.lumos_api.assignmrnt.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "assignments")
@Getter @Setter
@NoArgsConstructor 
@AllArgsConstructor
@Builder
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String course;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String deadline;

    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted;
}