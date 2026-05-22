package com.group4.lumos_api.Language_Exams.service;

import com.group4.lumos_api.Language_Exams.dto.LanguageExamsRequestDto;
import com.group4.lumos_api.Language_Exams.dto.LanguageExamsResponseDto;
import com.group4.lumos_api.Language_Exams.entity.LanguageExams;
import com.group4.lumos_api.Language_Exams.repository.LanguageExamsRepository;
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
public class LanguageExamsService {

    private final LanguageExamsRepository languageExamsRepository;
    private final UsersRepository usersRepository;

    @Transactional(readOnly = true)
    public List<LanguageExamsResponseDto> getExamsByStudent(String userId) {
        return languageExamsRepository.findByUserUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public LanguageExamsResponseDto addExam(LanguageExamsRequestDto dto) {
        Users user = usersRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        LanguageExams exam = LanguageExams.builder()
                .examCategory(dto.getExamCategory())
                .score(dto.getScore())
                .examDate(dto.getExamDate())
                .year(dto.getYear())
                .semester(dto.getSemester())
                .expiryDate(dto.getExpiryDate())
                .user(user)
                .build();
        return convertToDto(languageExamsRepository.save(exam));
    }

    @Transactional
    public LanguageExamsResponseDto updateExam(Long examId, LanguageExamsRequestDto dto) {
        LanguageExams exam = languageExamsRepository.findById(examId)
                .orElseThrow(() -> new EntityNotFoundException("Language exam not found"));
        
        exam.setExamCategory(dto.getExamCategory());
        exam.setScore(dto.getScore());
        exam.setExamDate(dto.getExamDate());
        exam.setYear(dto.getYear());
        exam.setSemester(dto.getSemester());
        exam.setExpiryDate(dto.getExpiryDate());
        
        return convertToDto(exam);
    }

    @Transactional
    public void deleteExam(Long examId) {
        languageExamsRepository.deleteById(examId);
    }

    private LanguageExamsResponseDto convertToDto(LanguageExams exam) {
        return LanguageExamsResponseDto.builder()
                .examId(exam.getExamId())
                .examCategory(exam.getExamCategory())
                .score(exam.getScore())
                .examDate(exam.getExamDate())
                .year(exam.getYear())
                .semester(exam.getSemester())
                .expiryDate(exam.getExpiryDate())
                .userId(exam.getUser().getUserId())
                .build();
    }
}
