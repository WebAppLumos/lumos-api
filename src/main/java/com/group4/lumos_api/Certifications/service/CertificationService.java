package com.group4.lumos_api.Certifications.service;

import com.group4.lumos_api.Certifications.dto.CertificationRequestDto;
import com.group4.lumos_api.Certifications.dto.CertificationResponseDto;
import com.group4.lumos_api.Certifications.entity.Certifications;
import com.group4.lumos_api.Certifications.repository.CertificationRepository;
import com.group4.lumos_api.student.entity.Students;
import com.group4.lumos_api.student.repository.StudentsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CertificationService {

    private final CertificationRepository certificationRepository;
    private final StudentsRepository studentsRepository;

    @Transactional(readOnly = true)
    public List<CertificationResponseDto> getCertificationsByStudent(Long studentId) {
        return certificationRepository.findByStudentStudentID(studentId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CertificationResponseDto addCertification(CertificationRequestDto dto) {
        Students student = studentsRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));

        Certifications cert = Certifications.builder()
                .certName(dto.getCertName())
                .issueDate(dto.getIssueDate())
                .student(student)
                .build();
        return convertToDto(certificationRepository.save(cert));
    }

    @Transactional
    public CertificationResponseDto updateCertification(Long certId, CertificationRequestDto dto) {
        Certifications cert = certificationRepository.findById(certId)
                .orElseThrow(() -> new EntityNotFoundException("Certification not found"));
        
        cert.setCertName(dto.getCertName());
        cert.setIssueDate(dto.getIssueDate());
        
        return convertToDto(cert);
    }

    @Transactional
    public void deleteCertification(Long certId) {
        certificationRepository.deleteById(certId);
    }

    private CertificationResponseDto convertToDto(Certifications cert) {
        return CertificationResponseDto.builder()
                .certId(cert.getCertId())
                .certName(cert.getCertName())
                .issueDate(cert.getIssueDate())
                .studentId(cert.getStudent().getStudentID())
                .build();
    }
}
