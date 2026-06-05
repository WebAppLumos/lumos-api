package com.group4.lumos_api.user.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserRequestDto {
    private String userId;
    private String email;
    private String name;
    private String phoneNumber;
    private String major;
    private Integer grade;
    private String studentNumber;
    private String profileImageUrl;
}
