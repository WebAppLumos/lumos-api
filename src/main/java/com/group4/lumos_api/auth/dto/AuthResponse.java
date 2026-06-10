package com.group4.lumos_api.auth.dto;

import com.group4.lumos_api.user.dto.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AuthResponse {
    // 인증에 사용할 토큰은 클라이언트가 보유한 Firebase ID 토큰을 그대로 사용한다.
    // (서버는 별도 토큰을 발급하지 않으며, 만료 시 Firebase 클라이언트 SDK가 ID 토큰을 자동 갱신한다.)
    private UserResponseDto user;
}
