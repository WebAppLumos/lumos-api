package com.group4.lumos_api.previous_semester_scores.service;

import com.group4.lumos_api.previous_semester_scores.dto.ScoreRequestDto;
import com.group4.lumos_api.previous_semester_scores.dto.ScoreResponseDto;
import com.group4.lumos_api.previous_semester_scores.entity.PreviousSemesterScores;
import com.group4.lumos_api.previous_semester_scores.repository.PreviousSemesterScoresRepository;
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
public class PreviousSemesterScoresService {

    private final PreviousSemesterScoresRepository scoresRepository;
    private final UsersRepository usersRepository;

    @Transactional(readOnly = true)
    public List<ScoreResponseDto> getScoresByUser(String userId) {
        return scoresRepository.findByUserUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ScoreResponseDto addScore(String userId, ScoreRequestDto dto) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        PreviousSemesterScores score = PreviousSemesterScores.builder()
                .user(user)
                .score(dto.getScore())
                .year(dto.getYear())
                .semester(dto.getSemester())
                .build();
        return convertToDto(scoresRepository.save(score));
    }

    @Transactional
    public ScoreResponseDto updateScore(Long scoreId, ScoreRequestDto dto) {
        PreviousSemesterScores score = scoresRepository.findById(scoreId)
                .orElseThrow(() -> new EntityNotFoundException("Score not found with id: " + scoreId));

        score.setScore(dto.getScore());
        score.setYear(dto.getYear());
        score.setSemester(dto.getSemester());

        return convertToDto(score);
    }

    @Transactional
    public void deleteScore(Long scoreId) {
        if (!scoresRepository.existsById(scoreId)) {
            throw new EntityNotFoundException("Score not found with id: " + scoreId);
        }
        scoresRepository.deleteById(scoreId);
    }

    private ScoreResponseDto convertToDto(PreviousSemesterScores score) {
        return ScoreResponseDto.builder()
                .gradeId(score.getGradeId())
                .userId(score.getUser().getUserId())
                .score(score.getScore())
                .year(score.getYear())
                .semester(score.getSemester())
                .createdAt(score.getCreatedAt())
                .updatedAt(score.getUpdatedAt())
                .build();
    }
}
