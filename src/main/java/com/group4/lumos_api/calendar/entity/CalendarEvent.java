package com.group4.lumos_api.calendar.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "calendar")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private com.group4.lumos_api.user.entity.Users user;

    @Column(name = "is_completed", nullable = false)
    @Builder.Default
    private boolean isCompleted = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    @Builder.Default
    private EventCategory category = EventCategory.OTHER;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    @Builder.Default
    private EventPriority priority = EventPriority.MEDIUM;
}
