package com.group4.lumos_api.auth.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.group4.lumos_api.auth.config.FirebaseConfig;
import com.group4.lumos_api.auth.dto.AuthLoginRequest;
import com.group4.lumos_api.auth.dto.AuthResponse;
import com.group4.lumos_api.user.dto.UserResponseDto;
import com.group4.lumos_api.user.entity.Users;
import com.group4.lumos_api.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepository usersRepository;

    @Transactional
    public AuthResponse login(AuthLoginRequest request) {
        FirebaseToken token = verifyIdToken(request.getIdToken());
        Users user = usersRepository.findById(token.getUid())
                .map(existingUser -> updateUser(existingUser, token, request))
                .orElseGet(() -> createUser(token, request));

        Users savedUser = usersRepository.save(user);
        return AuthResponse.builder()
                .accessToken(createCustomToken(savedUser.getUserId()))
                .tokenType("FirebaseCustomToken")
                .user(toUserResponse(savedUser))
                .build();
    }

    @Transactional
    public void logout(String idToken) {
        FirebaseToken token = verifyIdToken(idToken);
        try {
            FirebaseAuth.getInstance().revokeRefreshTokens(token.getUid());
        } catch (FirebaseAuthException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to revoke Firebase token", e);
        }
    }

    @Transactional(readOnly = true)
    public AuthResponse refresh(String idToken) {
        FirebaseToken token = verifyIdToken(idToken, true);
        Users user = usersRepository.findById(token.getUid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return AuthResponse.builder()
                .accessToken(createCustomToken(user.getUserId()))
                .tokenType("FirebaseCustomToken")
                .user(toUserResponse(user))
                .build();
    }

    private FirebaseToken verifyIdToken(String idToken) {
        return verifyIdToken(idToken, false);
    }

    private FirebaseToken verifyIdToken(String idToken, boolean checkRevoked) {
        try {
            FirebaseConfig.initialize();
            return FirebaseAuth.getInstance().verifyIdToken(idToken, checkRevoked);
        } catch (FirebaseAuthException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Firebase ID token", e);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Firebase credentials are not configured", e);
        }
    }

    private String createCustomToken(String uid) {
        try {
            FirebaseConfig.initialize();
            return FirebaseAuth.getInstance().createCustomToken(uid);
        } catch (FirebaseAuthException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to create Firebase custom token", e);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Firebase credentials are not configured", e);
        }
    }

    private Users createUser(FirebaseToken token, AuthLoginRequest request) {
        return Users.builder()
                .userId(token.getUid())
                .email(resolveEmail(token))
                .name(resolveName(token, request))
                .phoneNumber(request.getPhoneNumber())
                .department(request.getDepartment())
                .grade(request.getGrade())
                .studentNumber(request.getStudentNumber())
                .build();
    }

    private Users updateUser(Users user, FirebaseToken token, AuthLoginRequest request) {
        user.setEmail(resolveEmail(token));
        user.setName(resolveName(token, request));
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getDepartment() != null) {
            user.setDepartment(request.getDepartment());
        }
        if (request.getGrade() != null) {
            user.setGrade(request.getGrade());
        }
        if (request.getStudentNumber() != null) {
            user.setStudentNumber(request.getStudentNumber());
        }
        return user;
    }

    private String resolveEmail(FirebaseToken token) {
        if (token.getEmail() == null || token.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Firebase token does not contain email");
        }
        return token.getEmail();
    }

    private String resolveName(FirebaseToken token, AuthLoginRequest request) {
        if (request.getName() != null && !request.getName().isBlank()) {
            return request.getName();
        }
        String tokenName = token.getName();
        if (tokenName != null && !tokenName.isBlank()) {
            return tokenName;
        }
        return resolveEmail(token);
    }

    private UserResponseDto toUserResponse(Users user) {
        return UserResponseDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .department(user.getDepartment())
                .grade(user.getGrade())
                .studentNumber(user.getStudentNumber())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
