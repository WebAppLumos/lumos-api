package com.group4.lumos_api.semester.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "semesters")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Semester {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 학기명 (예: "2024-1학기", "2024 Spring")
    @Column(nullable = false, length = 100)
    private String name;
    
    // 시작 날짜
    @Column(nullable = false)
    private LocalDate startDate;
    
    // 종료 날짜
    @Column(nullable = false)
    private LocalDate endDate;
    
    // 활성 여부
    @Column(nullable = false)
    private Boolean isActive = true;
    
    // 생성 일시
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // 수정 일시
    @Column(nullable = false)
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
