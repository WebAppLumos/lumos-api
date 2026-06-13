package com.group4.lumos_api.certifications.service;

import com.group4.lumos_api.certifications.dto.CertificationRequestDto;
import com.group4.lumos_api.certifications.dto.CertificationResponseDto;
import com.group4.lumos_api.certifications.entity.Certifications;
import com.group4.lumos_api.certifications.repository.CertificationRepository;
import com.group4.lumos_api.user.entity.Users;
import com.group4.lumos_api.user.repository.UsersRepository;
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
    private final UsersRepository usersRepository;

    @Transactional(readOnly = true)
    public List<CertificationResponseDto> getCertificationsByStudent(String userId) {
        return certificationRepository.findByUserUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CertificationResponseDto addCertification(CertificationRequestDto dto) {
        Users user = usersRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Certifications cert = Certifications.builder()
                .certName(dto.getCertName())
                .issueDate(dto.getIssueDate())
                .user(user)
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
        Certifications cert = certificationRepository.findById(certId)
                .orElseThrow(() -> new EntityNotFoundException("Certification not found"));

        certificationRepository.delete(cert);
    }

    private CertificationResponseDto convertToDto(Certifications cert) {
        return CertificationResponseDto.builder()
                .certId(cert.getCertId())
                .certName(cert.getCertName())
                .issueDate(cert.getIssueDate())
                .userId(cert.getUser().getUserId())
                .build();
    }
}
