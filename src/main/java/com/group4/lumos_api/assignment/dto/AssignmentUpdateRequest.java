package com.group4.lumos_api.assignment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AssignmentUpdateRequest {
    @Size(min = 1, message = "과목명은 비어있을 수 없습니다.")
    private String course;
    
    @Size(min = 1, message = "과제명은 비어있을 수 없습니다.")
    private String title;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime deadline;
    
    private Boolean isCompleted;
}
