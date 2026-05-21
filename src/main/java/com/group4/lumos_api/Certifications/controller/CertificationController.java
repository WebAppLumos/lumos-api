package com.group4.lumos_api.Certifications.controller;

import com.group4.lumos_api.Certifications.dto.CertificationRequestDto;
import com.group4.lumos_api.Certifications.dto.CertificationResponseDto;
import com.group4.lumos_api.Certifications.service.CertificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certifications")
@RequiredArgsConstructor
public class CertificationController {

    private final CertificationService certificationService;

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<CertificationResponseDto>> getCertifications(@PathVariable Long studentId) {
        return ResponseEntity.ok(certificationService.getCertificationsByStudent(studentId));
    }

    @PostMapping
    public ResponseEntity<CertificationResponseDto> addCertification(@RequestBody CertificationRequestDto dto) {
        return ResponseEntity.ok(certificationService.addCertification(dto));
    }

    @PutMapping("/{certId}")
    public ResponseEntity<CertificationResponseDto> updateCertification(
            @PathVariable Long certId, 
            @RequestBody CertificationRequestDto dto) {
        return ResponseEntity.ok(certificationService.updateCertification(certId, dto));
    }

    @DeleteMapping("/{certId}")
    public ResponseEntity<Void> deleteCertification(@PathVariable Long certId) {
        certificationService.deleteCertification(certId);
        return ResponseEntity.noContent().build();
    }
}
