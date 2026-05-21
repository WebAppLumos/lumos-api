package com.group4.lumos_api.Certifications.repository;

import com.group4.lumos_api.Certifications.entity.Certifications;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CertificationRepository extends JpaRepository<Certifications, Long> {
    List<Certifications> findByStudentStudentID(Long studentId);
}
