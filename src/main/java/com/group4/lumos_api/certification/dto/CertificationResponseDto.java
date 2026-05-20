package com.group4.lumos_api.certification.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificationResponseDto {
    private Long certId;
    private String certName;
    private LocalDate issueDate;
    private Long studentId;
}
