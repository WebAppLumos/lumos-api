package com.group4.lumos_api.Certifications.controller;

import com.group4.lumos_api.Certifications.dto.CertificationRequestDto;
import com.group4.lumos_api.Certifications.dto.CertificationResponseDto;
import com.group4.lumos_api.Certifications.service.CertificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CertificationController {

    private final CertificationService certificationService;

    @GetMapping("/users/{userId}/certifications")
    public ResponseEntity<List<CertificationResponseDto>> getCertifications(@PathVariable String userId) {
        return ResponseEntity.ok(certificationService.getCertificationsByStudent(userId));
    }

    @PostMapping("/users/{userId}/certifications")
    public ResponseEntity<CertificationResponseDto> addCertification(
            @PathVariable String userId,
            @RequestBody CertificationRequestDto dto) {
        dto.setUserId(userId);
        return ResponseEntity.ok(certificationService.addCertification(dto));
    }

    @PatchMapping("/certifications/{certId}")
    public ResponseEntity<CertificationResponseDto> updateCertification(
            @PathVariable Long certId, 
            @RequestBody CertificationRequestDto dto) {
        return ResponseEntity.ok(certificationService.updateCertification(certId, dto));
    }

    @DeleteMapping("/certifications/{certId}")
    public ResponseEntity<Void> deleteCertification(@PathVariable Long certId) {
        certificationService.deleteCertification(certId);
        return ResponseEntity.noContent().build();
    }
}
