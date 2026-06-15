package com.group4.lumos_api.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.group4.lumos_api.user.entity.Users;
import lombok.*;
import java.time.LocalDateTime;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class UserResponseDto {
    private String userId;
    private String email;
    private String name;
    private String phoneNumber;
    @JsonProperty("department")
    private String major;
    private Integer grade;
    private String studentNumber;
    private String profileImage;
    private Integer incomeBracket;
    private Boolean scholarshipCurationCompleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 💡 엔티티를 받아서 초기화하는 생성자 추가
    public UserResponseDto(Users user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.phoneNumber = user.getPhoneNumber();
        this.major = user.getMajor();
        this.grade = user.getGrade();
        this.studentNumber = user.getStudentNumber();
        this.profileImage = user.getProfileImage();
        this.incomeBracket = user.getIncomeBracket();
        this.scholarshipCurationCompleted = user.getScholarshipCurationCompleted();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }

    @JsonIgnore
    public String getDepartment() { return major; }
    public void setDepartment(String department) { this.major = department; }

    public static class UserResponseDtoBuilder {
        public UserResponseDtoBuilder department(String department) {
            this.major = department;
            return this;
        }
    }
}
