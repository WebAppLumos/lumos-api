package com.group4.lumos_api.semester.entity;

import com.group4.lumos_api.user.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "semester")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Semester {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "semester_id")
    private Long id;

    // 소유자 (이 학기가 귀속된 사용자)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private Users user;
    
    // 학기명 (예: "2024-1학기", "2024 Spring")
    @Column(name = "title", nullable = false, length = 100)
    private String title;
    
    // 시작 날짜
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    // 종료 날짜
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    // 활성 여부
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    // 생성 일시
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // 수정 일시
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        // 엔티티 처음 생성될 때만 실행
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        // 엔티티 수정될 때마다 실행
        updatedAt = LocalDateTime.now();
    }
}
