package com.group4.lumos_api.semester_grades.entity;

import com.group4.lumos_api.user.entity.Users;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "semester_grades",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_semester_grades_user_term",
                columnNames = {"user_id", "academic_year", "term_code"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SemesterGrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "semester_grade_id")
    private Long semesterGradeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "academic_year", nullable = false)
    private Integer academicYear;

    @Column(name = "term_code", nullable = false, length = 2)
    private String termCode;

    @Column(name = "term_name", nullable = false, length = 20)
    private String termName;

    @Column(name = "completed_credits", nullable = false)
    private Integer completedCredits;

    @Column(name = "registered_credits", nullable = false)
    private Integer registeredCredits;

    @Column(name = "gpa", nullable = false)
    private Double gpa;

    @Column(name = "academic_warning", nullable = false)
    private Boolean academicWarning;

    @Column(name = "synced_at", nullable = false)
    private LocalDateTime syncedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (syncedAt == null) {
            syncedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
