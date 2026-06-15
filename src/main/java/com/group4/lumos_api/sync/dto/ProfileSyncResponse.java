package com.group4.lumos_api.sync.dto;

import com.group4.lumos_api.user.dto.UserResponseDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProfileSyncResponse {

    private final String studentNumber;
    private final String major;
    private final Integer grade;
    private final UserResponseDto user;
}
