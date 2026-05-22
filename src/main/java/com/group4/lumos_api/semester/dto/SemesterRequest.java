package com.group4.lumos_api.semester.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SemesterRequest {
    // 학기명
    private String title;
    
    // 시작 날짜
    private LocalDate startDate;
    
    // 종료 날짜
    private LocalDate endDate;
    
    // 활성 여부
    private Boolean isActive;
}
