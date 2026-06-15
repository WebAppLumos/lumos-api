package com.group4.lumos_api.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthLoginRequest {
    @NotBlank
    private String idToken;

    private String name;
    private String phoneNumber;
    private String department;
    private Integer grade;
    private String studentNumber;
    private String incomeBracket;
}
