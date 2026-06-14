package com.group4.lumos_api.user.service;

import com.group4.lumos_api.assignment.repository.AssignmentRepository;
import com.group4.lumos_api.calendar.repository.CalendarEventRepository;
import com.group4.lumos_api.certifications.repository.CertificationsRepository;
import com.group4.lumos_api.dashboard.service.DashboardWidgetService;
import com.group4.lumos_api.language_exams.repository.LanguageExamsRepository;
import com.group4.lumos_api.previous_semester_scores.repository.PreviousSemesterScoresRepository;
import com.group4.lumos_api.semester.repository.SemesterRepository;
import com.group4.lumos_api.semester.service.SemesterService;
import com.group4.lumos_api.semester_grades.repository.SemesterGradeRepository;
import com.group4.lumos_api.user.dto.UserRequestDto;
import com.group4.lumos_api.user.dto.UserResponseDto;
import com.group4.lumos_api.user.entity.Users;
import com.group4.lumos_api.user.repository.UsersRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository usersRepository;
    private final CalendarEventRepository calendarEventRepository;
    private final DashboardWidgetService dashboardWidgetService;
    private final SemesterRepository semesterRepository;
    private final SemesterService semesterService;
    private final SemesterGradeRepository semesterGradeRepository;
    private final AssignmentRepository assignmentRepository;
    private final LanguageExamsRepository languageExamsRepository;
    private final CertificationsRepository certificationsRepository;
    private final PreviousSemesterScoresRepository previousSemesterScoresRepository;
    private final EntityManager entityManager;

    // 1. 회원가입 (중복 방어 로직)
    @Transactional
    public UserResponseDto createUser(UserRequestDto dto) {
        if (usersRepository.existsByStudentNumber(dto.getStudentNumber())) {
            throw new IllegalArgumentException("이미 등록된 학번입니다.");
        }
        if (usersRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new IllegalArgumentException("이미 등록된 전화번호입니다.");
        }
        if (usersRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }

        Users user = Users.builder()
                .userId(dto.getUserId())
                .email(dto.getEmail())
                .name(dto.getName())
                .phoneNumber(dto.getPhoneNumber())
                .major(dto.getMajor())
                .grade(dto.getGrade())
                .studentNumber(dto.getStudentNumber())
                .profileImage(dto.getProfileImage())
                .build();

        Users saved = usersRepository.saveAndFlush(user);
        entityManager.refresh(saved);
        return new UserResponseDto(saved);
    }

    // 2. 전체 사용자 조회
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return usersRepository.findAll().stream()
                .map(UserResponseDto::new)
                .collect(Collectors.toList());
    }

    // 3. ID로 조회
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(String userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return new UserResponseDto(user);
    }

    // 4. 정보 수정
    @Transactional
    public UserResponseDto updateUser(String userId, UserRequestDto dto) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.setName(dto.getName());
        user.setMajor(dto.getMajor());
        user.setGrade(dto.getGrade());
        user.setPhoneNumber(dto.getPhoneNumber());

        return new UserResponseDto(usersRepository.save(user));
    }

    // 5. 회원 탈퇴
    @Transactional
    public void deleteUser(String userId) {

        if (!usersRepository.existsById(userId)) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        List<Long> semesterIds = semesterRepository.findAllByUser_UserIdOrderBySortOrderAscIdAsc(userId)
                .stream()
                .map(semester -> semester.getId())
                .toList();
        for (Long semesterId : semesterIds) {
            semesterService.deleteSemester(userId, semesterId);
        }

        semesterGradeRepository.deleteAllByUser_UserId(userId);
        calendarEventRepository.deleteByUser_UserId(userId);
        dashboardWidgetService.deleteWidgetsByUserId(userId);
        assignmentRepository.deleteAll(assignmentRepository.findAllByUserId(userId));
        languageExamsRepository.deleteAll(languageExamsRepository.findByUserUserId(userId));
        certificationsRepository.deleteAll(certificationsRepository.findByUserUserId(userId));
        previousSemesterScoresRepository.deleteAll(
                previousSemesterScoresRepository.findByUserUserId(userId));

        usersRepository.deleteById(userId);
    }

    // 6. 프로필 이미지 변경
    @Transactional
    public UserResponseDto updateProfileImage(String userId, String imageUrl) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        System.out.println(">>> UserService: 프로필 이미지 업데이트 시도 (길이: "
                + (imageUrl != null ? imageUrl.length() : 0) + ")");

        try {
            user.setProfileImage(imageUrl);
            Users saved = usersRepository.save(user);

            System.out.println(">>> UserService: 프로필 이미지 업데이트 성공");

            return new UserResponseDto(saved);

        } catch (Exception e) {
            System.err.println(">>> UserService: 프로필 이미지 업데이트 실패! 에러: "
                    + e.getMessage());
            throw e;
        }
    }
}
