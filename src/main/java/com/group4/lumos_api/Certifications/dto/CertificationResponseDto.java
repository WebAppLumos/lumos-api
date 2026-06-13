package com.group4.lumos_api.certifications.dto;

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
    private String userId;
}
