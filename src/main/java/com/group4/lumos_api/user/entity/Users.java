package com.group4.lumos_api.user.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Users {
    @Id
    @Column(name = "user_id", length = 255)
    private String userId;

    @Column(name = "email", length = 255, unique = true, nullable = false)
    private String email;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "phone_number", length = 30, unique = true, nullable = false)
    private String phoneNumber;

    @Column(name = "major", length = 100, nullable = false)
    private String major;

    @Column(name = "grade", nullable = false)
    private Integer grade;

    @Column(name = "student_number", length = 7, unique = true, nullable = false)
    private String studentNumber;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public String getDepartment() { return major; }
    public void setDepartment(String department) { this.major = department; }

    public static class UsersBuilder {
        public UsersBuilder department(String department) {
            this.major = department;
            return this;
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}