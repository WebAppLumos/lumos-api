package com.group4.lumos_api.Certifications.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificationRequestDto {
    private String certName;
    private LocalDate issueDate;
    private Long studentId;
}
