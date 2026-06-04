package com.group4.lumos_api.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthTokenRequest {
    @NotBlank
    private String idToken;
}
