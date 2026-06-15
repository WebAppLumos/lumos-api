package com.group4.lumos_api.auth.service;

import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.group4.lumos_api.auth.config.FirebaseConfig;
import com.group4.lumos_api.auth.dto.AuthLoginRequest;
import com.group4.lumos_api.auth.dto.AuthResponse;
import com.group4.lumos_api.common.exception.BadRequestException;
import com.group4.lumos_api.common.exception.ConflictException;
import com.group4.lumos_api.common.exception.NotFoundException;
import com.group4.lumos_api.dashboard.service.DashboardWidgetService;
import com.group4.lumos_api.user.dto.UserResponseDto;
import com.group4.lumos_api.user.entity.Users;
import com.group4.lumos_api.user.repository.UsersRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UsersRepository usersRepository;
    private final EntityManager entityManager;
    private final DashboardWidgetService dashboardWidgetService;

    @Transactional
    public AuthResponse login(AuthLoginRequest request) {
        FirebaseToken token = verifyIdToken(request.getIdToken());
        boolean isNewUser = !usersRepository.existsById(token.getUid());
        Users user = usersRepository.findById(token.getUid())
                .map(existingUser -> updateUser(existingUser, token, request))
                .orElseGet(() -> createUserForSignup(token, request));

        Users savedUser = usersRepository.saveAndFlush(user);
        entityManager.refresh(savedUser);

        if (isNewUser) {
            dashboardWidgetService.getWidgets(savedUser.getUserId());
        }
        // 별도 토큰을 발급하지 않는다. 클라이언트는 로그인에 사용한 Firebase ID 토큰을
        // 이후 요청의 Authorization: Bearer 헤더에 그대로 사용한다.
        return AuthResponse.builder()
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

    /**
     * Firebase Authentication 계정을 Admin SDK로 삭제한다.
     * DB 삭제가 커밋된 뒤 호출되어, Firebase만 삭제되고 DB가 남는 상황을 방지한다.
     */
    public void deleteFirebaseAccount(String uid) {
        try {
            FirebaseConfig.initialize();
            FirebaseAuth.getInstance().deleteUser(uid);
        } catch (FirebaseAuthException e) {
            if (e.getAuthErrorCode() == AuthErrorCode.USER_NOT_FOUND) {
                log.warn("Firebase user not found during deletion (uid={}). Client fallback may be required.", uid);
                return;
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Firebase 계정 삭제에 실패했습니다.", e);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Firebase credentials are not configured", e);
        }
    }

    /**
     * 전달된 Firebase ID 토큰의 유효성(폐기 여부 포함)을 재검증하고 현재 사용자 정보를 반환한다.
     *
     * <p>Firebase ID 토큰의 실제 갱신은 클라이언트 SDK가 담당하므로 서버는 새 토큰을 발급하지 않는다.
     * 이 엔드포인트는 세션 유효성 확인/프로필 동기화 용도다.</p>
     */
    @Transactional(readOnly = true)
    public AuthResponse refresh(String idToken) {
        FirebaseToken token = verifyIdToken(idToken, true);
        Users user = usersRepository.findById(token.getUid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return AuthResponse.builder()
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

    private Users createUserForSignup(FirebaseToken token, AuthLoginRequest request) {
        if (!hasSignupProfile(request)) {
            throw new NotFoundException("등록된 회원 정보가 없습니다. 회원가입을 진행해 주세요.");
        }

        return createUser(token, request);
    }

    private boolean hasSignupProfile(AuthLoginRequest request) {
        return trimToNull(request.getName()) != null
                && trimToNull(request.getPhoneNumber()) != null;
    }

    private Users createUser(FirebaseToken token, AuthLoginRequest request) {
        validateCreateRequest(token, request);

        return Users.builder()
                .userId(token.getUid())
                .email(resolveEmail(token))
                .name(resolveName(token, request))
                .phoneNumber(trimToNull(request.getPhoneNumber()))
                .department(trimToNull(request.getDepartment()))
                .grade(request.getGrade())
                .studentNumber(trimToNull(request.getStudentNumber()))
                .incomeBracket(request.getIncomeBracket())
                .build();
    }

    private Users updateUser(Users user, FirebaseToken token, AuthLoginRequest request) {
        user.setEmail(resolveEmail(token));
        String requestName = trimToNull(request.getName());
        if (requestName != null) {
            user.setName(requestName);
        }
        if (trimToNull(request.getPhoneNumber()) != null) {
            user.setPhoneNumber(trimToNull(request.getPhoneNumber()));
        }
        if (trimToNull(request.getDepartment()) != null) {
            user.setDepartment(trimToNull(request.getDepartment()));
        }
        if (request.getGrade() != null) {
            user.setGrade(request.getGrade());
        }
        if (trimToNull(request.getStudentNumber()) != null) {
            user.setStudentNumber(trimToNull(request.getStudentNumber()));
        }
        if (request.getIncomeBracket() != null) {
            user.setIncomeBracket(request.getIncomeBracket());
        }
        return user;
    }

    private void validateCreateRequest(FirebaseToken token, AuthLoginRequest request) {
        String email = resolveEmail(token);
        String name = trimToNull(request.getName());
        String phoneNumber = trimToNull(request.getPhoneNumber());

        if (name == null) {
            throw new BadRequestException("이름을 입력해 주세요.");
        }
        if (phoneNumber == null) {
            throw new BadRequestException("전화번호를 입력해 주세요.");
        }
        if (usersRepository.existsByEmail(email)) {
            throw new ConflictException("이미 등록된 이메일입니다.");
        }
        if (usersRepository.existsByPhoneNumber(phoneNumber)) {
            throw new ConflictException("이미 등록된 전화번호입니다.");
        }
        String studentNumber = trimToNull(request.getStudentNumber());
        if (studentNumber != null && usersRepository.existsByStudentNumber(studentNumber)) {
            throw new ConflictException("이미 등록된 학번입니다.");
        }
    }

    private String resolveEmail(FirebaseToken token) {
        if (token.getEmail() == null || token.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Firebase token does not contain email");
        }
        return token.getEmail();
    }

    private String resolveName(FirebaseToken token, AuthLoginRequest request) {
        String requestName = trimToNull(request.getName());
        if (requestName != null) {
            return requestName;
        }
        String tokenName = token.getName();
        if (tokenName != null && !tokenName.isBlank()) {
            return tokenName;
        }
        return resolveEmail(token);
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
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
                .profileImage(user.getProfileImage())
                .incomeBracket(user.getIncomeBracket())
                .scholarshipCurationCompleted(user.getScholarshipCurationCompleted())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
