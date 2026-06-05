package com.group4.lumos_api.user.controller;

import com.group4.lumos_api.user.dto.UserRequestDto;
import com.group4.lumos_api.user.dto.UserResponseDto;
import com.group4.lumos_api.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto dto) {
        return ResponseEntity.ok(userService.createUser(dto));
    }

    // 내 정보 조회 (임시로 userId를 넘겨받거나 인증 객체에서 추출해야 함. 여기서는 @RequestHeader 등을 고려할 수 있으나 명세대로 구현)
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMyInfo(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    // 내 정보 수정
    @PatchMapping("/me")
    public ResponseEntity<UserResponseDto> updateMyInfo(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody UserRequestDto dto) {
        return ResponseEntity.ok(userService.updateUser(userId, dto));
    }

    // 회원 탈퇴
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteAccount(@RequestHeader("X-User-Id") String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    // 프로필 이미지 변경
    @PatchMapping("/me/profile-image")
    public ResponseEntity<UserResponseDto> updateProfileImage(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody UserRequestDto dto) {
        return ResponseEntity.ok(userService.updateProfileImage(userId, dto.getProfileImageUrl()));
    }

    // 알림 설정 조회 (Entity에 관련 필드가 추가되어야 할 수도 있음)
    @GetMapping("/me/settings")
    public ResponseEntity<Object> getMySettings(@RequestHeader("X-User-Id") String userId) {
        // 임시 반환
        return ResponseEntity.ok(new Object());
    }

    // 알림 설정 수정
    @PatchMapping("/me/settings")
    public ResponseEntity<Object> updateMySettings(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody Object settings) {
        // 임시 반환
        return ResponseEntity.ok(settings);
    }

    // 관리자용 전체 목록 조회
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
