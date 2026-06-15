package com.group4.lumos_api.sync.service;

import com.group4.lumos_api.common.exception.BadRequestException;
import com.group4.lumos_api.common.exception.ConflictException;
import com.group4.lumos_api.user.entity.Users;
import com.group4.lumos_api.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * EDWARD 동기화 시 Lumos 사용자 학번과 EDWARD 학번을 맞춘다.
 * 회원가입 직후 학번이 비어 있으면 EDWARD에서 가져온 학번을 최초 1회 저장한다.
 */
@Component
@RequiredArgsConstructor
public class EdwardStudentNumberGuard {

    private final UsersRepository usersRepository;

    public void ensureMatchesOrAssign(Users user, String edwardStudentNumber) {
        String normalized = normalizeStudentNumber(edwardStudentNumber);
        String current = trimToNull(user.getStudentNumber());

        if (current == null) {
            if (usersRepository.existsByStudentNumber(normalized)) {
                throw new ConflictException("이미 다른 계정에 등록된 학번입니다.");
            }
            user.setStudentNumber(normalized);
            usersRepository.save(user);
            return;
        }

        if (!current.equals(normalized)) {
            throw new BadRequestException("EDWARD 로그인 학번이 내 학번과 일치해야 합니다.");
        }
    }

    private static String normalizeStudentNumber(String value) {
        String normalized = trimToNull(value);
        if (normalized == null || !normalized.matches("\\d{7}")) {
            throw new BadRequestException("EDWARD 학번 형식이 올바르지 않습니다.");
        }
        return normalized;
    }

    private static String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
