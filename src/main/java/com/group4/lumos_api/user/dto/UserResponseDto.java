package com.group4.lumos_api.user.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserResponseDto {
    private String userId;
    private String email;
    private String name;
    private String phoneNumber;
    private String department;
    private Integer grade;
    private String studentNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
