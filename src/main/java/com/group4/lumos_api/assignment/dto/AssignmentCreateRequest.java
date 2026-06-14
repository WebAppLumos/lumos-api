package com.group4.lumos_api.assignment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AssignmentCreateRequest {
    @NotBlank(message = "과목명은 필수 입력 값입니다.")
    private String course;
    
    @NotBlank(message = "과제명은 필수 입력 값입니다.")
    private String title;
    
    @NotNull(message = "마감일은 필수 입력 값입니다.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime deadline;
    
    private Boolean isCompleted;
}
