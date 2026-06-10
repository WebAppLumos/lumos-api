package com.group4.lumos_api.user.controller;

import com.group4.lumos_api.common.security.CurrentUser;
import com.group4.lumos_api.user.dto.UserRequestDto;
import com.group4.lumos_api.user.dto.UserResponseDto;
import com.group4.lumos_api.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 내 정보 조회
     * GET /api/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMe(@CurrentUser String userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    /**
     * 내 정보 수정
     * PATCH /api/users/me
     */
    @PatchMapping("/me")
    public ResponseEntity<UserResponseDto> updateMe(
            @CurrentUser String userId,
            @RequestBody UserRequestDto dto) {
        return ResponseEntity.ok(userService.updateUser(userId, dto));
    }

    /**
     * 회원 탈퇴
     * DELETE /api/users/me
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(@CurrentUser String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
