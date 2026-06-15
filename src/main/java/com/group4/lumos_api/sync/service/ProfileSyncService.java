package com.group4.lumos_api.sync.service;

import com.group4.lumos_api.common.exception.BadRequestException;
import com.group4.lumos_api.common.exception.NotFoundException;
import com.group4.lumos_api.sync.dto.ProfileImportRequest;
import com.group4.lumos_api.sync.dto.ProfileSyncResponse;
import com.group4.lumos_api.user.dto.UserResponseDto;
import com.group4.lumos_api.user.entity.Users;
import com.group4.lumos_api.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileSyncService {

    private final UsersRepository usersRepository;
    private final EdwardStudentNumberGuard edwardStudentNumberGuard;

    public ProfileSyncResponse importFromEdward(String userId, ProfileImportRequest request) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        edwardStudentNumberGuard.ensureMatchesOrAssign(user, request.getStudentNumber());

        String major = request.getMajor().trim();
        if (major.isEmpty()) {
            throw new BadRequestException("EDWARD 전공 정보가 비어 있습니다.");
        }

        user.setMajor(major);
        user.setGrade(request.getGrade());
        Users saved = usersRepository.save(user);

        return new ProfileSyncResponse(
                saved.getStudentNumber(),
                saved.getMajor(),
                saved.getGrade(),
                new UserResponseDto(saved)
        );
    }
}
