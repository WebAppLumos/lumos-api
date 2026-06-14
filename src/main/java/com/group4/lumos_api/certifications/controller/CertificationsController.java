package com.group4.lumos_api.certifications.controller;

import com.group4.lumos_api.certifications.dto.CertificationsRequestDto;
import com.group4.lumos_api.certifications.dto.CertificationsResponseDto;
import com.group4.lumos_api.certifications.service.CertificationsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CertificationsController {

    private final CertificationsService certificationsService;

    @GetMapping("/users/{userId}/certifications")
    public ResponseEntity<List<CertificationsResponseDto>> getCertifications(@PathVariable String userId) {
        return ResponseEntity.ok(certificationsService.getCertificationsByStudent(userId));
    }

    @PostMapping("/users/{userId}/certifications")
    public ResponseEntity<CertificationsResponseDto> addCertification(
            @PathVariable String userId,
            @RequestBody CertificationsRequestDto dto) {
        dto.setUserId(userId);
        return ResponseEntity.ok(certificationsService.addCertification(dto));
    }

    @PatchMapping("/certifications/{certId}")
    public ResponseEntity<CertificationsResponseDto> updateCertification(
            @PathVariable Long certId, 
            @RequestBody CertificationsRequestDto dto) {
        return ResponseEntity.ok(certificationsService.updateCertification(certId, dto));
    }

    @DeleteMapping("/certifications/{certId}")
    public ResponseEntity<Void> deleteCertification(@PathVariable Long certId) {
        certificationsService.deleteCertification(certId);
        return ResponseEntity.noContent().build();
    }
}
