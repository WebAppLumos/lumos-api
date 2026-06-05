package com.group4.lumos_api.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore; // 💡 무시 처리를 위한 임포트 추가
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
    private String major;
    private Integer grade;
    private String studentNumber;
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Compatibility methods for Auth module
    @JsonIgnore // 💡 Jackson이 이 Getter를 감지해서 JSON 필드로 만드는 것을 원천 차단합니다.
    public String getDepartment() {
        return major;
    }

    public void setDepartment(String department) {
        this.major = department;
    }

    // Custom builder method for compatibility
    public static class UserResponseDtoBuilder {
        public UserResponseDtoBuilder department(String department) {
            this.major = department;
            return this;
        }
    }
}