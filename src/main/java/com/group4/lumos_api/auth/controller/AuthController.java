package com.group4.lumos_api.auth.controller;

import com.group4.lumos_api.auth.dto.AuthLoginRequest;
import com.group4.lumos_api.auth.dto.AuthMessageResponse;
import com.group4.lumos_api.auth.dto.AuthResponse;
import com.group4.lumos_api.auth.dto.AuthTokenRequest;
import com.group4.lumos_api.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthLoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthMessageResponse> logout(@Valid @RequestBody AuthTokenRequest request) {
        authService.logout(request.getIdToken());
        return ResponseEntity.ok(new AuthMessageResponse("Logged out successfully"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody AuthTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request.getIdToken()));
    }
}
