package com.group4.lumos_api.certifications.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group4.lumos_api.certifications.dto.CertificationsRequestDto;
import com.group4.lumos_api.certifications.dto.CertificationsResponseDto;
import com.group4.lumos_api.certifications.entity.Certifications;
import com.group4.lumos_api.certifications.repository.CertificationsRepository;
import com.group4.lumos_api.common.exception.NotFoundException;
import com.group4.lumos_api.user.entity.Users;
import com.group4.lumos_api.user.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CertificationsService {

    private final CertificationsRepository certificationsRepository;
    private final UsersRepository usersRepository;

    @Transactional(readOnly = true)
    public List<CertificationsResponseDto> getCertificationsByStudent(String userId) {
        return certificationsRepository.findByUserUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CertificationsResponseDto addCertification(CertificationsRequestDto dto) {
        Users user = usersRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        Certifications cert = Certifications.builder()
                .certName(dto.getCertName())
                .issueDate(dto.getIssueDate())
                .user(user)
                .build();
        return convertToDto(certificationsRepository.save(cert));
    }

    @Transactional
    public CertificationsResponseDto updateCertification(Long certId, CertificationsRequestDto dto) {
        Certifications cert = certificationsRepository.findById(certId)
                .orElseThrow(() -> new NotFoundException("Certification not found"));

        if (dto.getCertName() != null) {
            cert.setCertName(dto.getCertName());
        }
        if (dto.getIssueDate() != null) {
            cert.setIssueDate(dto.getIssueDate());
        }

        return convertToDto(cert);
    }

    @Transactional
    public void deleteCertification(Long certId) {
        certificationsRepository.deleteById(certId);
    }

    private CertificationsResponseDto convertToDto(Certifications cert) {
        return CertificationsResponseDto.builder()
                .certId(cert.getCertId())
                .certName(cert.getCertName())
                .issueDate(cert.getIssueDate())
                .userId(cert.getUser().getUserId())
                .build();
    }
}
