package com.group4.lumos_api.sync.service;

import com.group4.lumos_api.common.exception.BadRequestException;
import com.group4.lumos_api.common.exception.NotFoundException;
import com.group4.lumos_api.semester_grades.entity.SemesterGrade;
import com.group4.lumos_api.semester_grades.repository.SemesterGradeRepository;
import com.group4.lumos_api.sync.client.EdwardSessionClient;
import com.group4.lumos_api.sync.client.SsvCodec;
import com.group4.lumos_api.sync.dto.GradeImportRequest;
import com.group4.lumos_api.sync.dto.GradeSyncResponse;
import com.group4.lumos_api.sync.model.EdwardSession;
import com.group4.lumos_api.sync.model.ParsedSemesterGrade;
import com.group4.lumos_api.sync.parser.SemesterGradeParser;
import com.group4.lumos_api.user.entity.Users;
import com.group4.lumos_api.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class GradeSyncService {

    private static final String GRADE_MENU_ID = "M503056";
    private static final String GRADE_PGM_ID = "P503007";

    private final EdwardSessionClient edwardSessionClient;
    private final SemesterGradeRepository semesterGradeRepository;
    private final UsersRepository usersRepository;
    private final EdwardStudentNumberGuard edwardStudentNumberGuard;

    public GradeSyncResponse importFromSsv(String userId, GradeImportRequest request) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        edwardStudentNumberGuard.ensureMatchesOrAssign(user, request.getStudentNumber());

        if (request.getSsv() == null || request.getSsv().isBlank()) {
            throw new BadRequestException("성적 데이터가 비어 있습니다.");
        }

        List<Map<String, String>> rows = SsvCodec.parseDatasetAllRows(request.getSsv(), "DS_SCOR210M01");
        if (rows.isEmpty()) {
            throw new BadRequestException("EDWARD에서 학기 성적을 찾지 못했습니다.");
        }

        List<ParsedSemesterGrade> parsedGrades = SemesterGradeParser.parseRows(rows);
        return upsertGrades(user, parsedGrades);
    }

    public GradeSyncResponse syncFromEdward(String userId, String loginName, char[] password) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        edwardStudentNumberGuard.ensureMatchesOrAssign(user, loginName);

        try (EdwardSession session = edwardSessionClient.login(loginName, password)) {
            edwardSessionClient.warmUpGlioSession(session);
            List<Map<String, String>> rows = edwardSessionClient.fetchSemesterGradeList(session, loginName);
            List<ParsedSemesterGrade> parsedGrades = SemesterGradeParser.parseRows(rows);
            return upsertGrades(user, parsedGrades);
        }
    }

    private GradeSyncResponse upsertGrades(Users user, List<ParsedSemesterGrade> parsedGrades) {
        LocalDateTime syncedAt = LocalDateTime.now();

        for (ParsedSemesterGrade parsed : parsedGrades) {
            SemesterGrade grade = semesterGradeRepository
                    .findByUser_UserIdAndAcademicYearAndTermCode(
                            user.getUserId(),
                            parsed.academicYear(),
                            parsed.termCode()
                    )
                    .orElseGet(() -> SemesterGrade.builder().user(user).build());

            grade.setAcademicYear(parsed.academicYear());
            grade.setTermCode(parsed.termCode());
            grade.setTermName(parsed.termName());
            grade.setCompletedCredits(parsed.completedCredits());
            grade.setRegisteredCredits(parsed.registeredCredits());
            grade.setGpa(parsed.gpa());
            grade.setAcademicWarning(parsed.academicWarning());
            grade.setSyncedAt(syncedAt);

            semesterGradeRepository.save(grade);
        }

        int totalCompletedCredits = parsedGrades.stream()
                .mapToInt(ParsedSemesterGrade::completedCredits)
                .sum();

        double weightedGpaSum = 0.0;
        int weightedCreditSum = 0;
        for (ParsedSemesterGrade parsed : parsedGrades) {
            if (parsed.completedCredits() > 0) {
                weightedGpaSum += parsed.gpa() * parsed.completedCredits();
                weightedCreditSum += parsed.completedCredits();
            }
        }

        double averageGpa = weightedCreditSum > 0
                ? weightedGpaSum / weightedCreditSum
                : 0.0;

        int academicWarningCount = (int) parsedGrades.stream()
                .filter(ParsedSemesterGrade::academicWarning)
                .count();

        return new GradeSyncResponse(
                parsedGrades.size(),
                totalCompletedCredits,
                averageGpa,
                academicWarningCount
        );
    }
}
