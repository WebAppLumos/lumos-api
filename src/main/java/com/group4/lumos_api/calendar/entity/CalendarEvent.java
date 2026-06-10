package com.group4.lumos_api.calendar.entity;

import com.group4.lumos_api.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    // 💡 단순 String에서 Users 객체 참조(FK) 구조로 변경
    // 학사 일정 생성을 위해 nullable = true(NULL 허용) 설정 필수
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private Users user;

    @Column(name = "title", length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "is_completed")
    private Boolean isCompleted;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 30)
    @Builder.Default
    private EventCategory category = EventCategory.OTHER;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 10)
    @Builder.Default
    private EventPriority priority = EventPriority.MEDIUM;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}