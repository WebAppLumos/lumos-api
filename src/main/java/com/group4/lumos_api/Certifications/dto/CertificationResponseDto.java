package com.group4.lumos_api.certifications.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificationResponseDto {
    private Long certId;
    private String certName;
    private LocalDate issueDate;
    private String userId;
}
