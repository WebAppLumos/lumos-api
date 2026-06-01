package com.group4.lumos_api.user.service;

import com.group4.lumos_api.user.dto.UserRequestDto;
import com.group4.lumos_api.user.dto.UserResponseDto;
import com.group4.lumos_api.user.entity.Users;
import com.group4.lumos_api.user.repository.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository usersRepository;

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return usersRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(String userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        return convertToDto(user);
    }

    @Transactional
    public UserResponseDto createUser(UserRequestDto dto) {
        Users user = Users.builder()
                .userId(dto.getUserId())
                .email(dto.getEmail())
                .name(dto.getName())
                .phoneNumber(dto.getPhoneNumber())
                .department(dto.getDepartment())
                .grade(dto.getGrade())
                .studentNumber(dto.getStudentNumber())
                .build();
        return convertToDto(usersRepository.save(user));
    }

    @Transactional
    public UserResponseDto updateUser(String userId, UserRequestDto dto) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setDepartment(dto.getDepartment());
        user.setGrade(dto.getGrade());
        user.setStudentNumber(dto.getStudentNumber());

        return convertToDto(user);
    }

    @Transactional
    public void deleteUser(String userId) {
        if (!usersRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }
        usersRepository.deleteById(userId);
    }

    private UserResponseDto convertToDto(Users user) {
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
