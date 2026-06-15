package com.group4.lumos_api.certifications.repository;

import com.group4.lumos_api.certifications.entity.Certifications;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CertificationsRepository extends JpaRepository<Certifications, Long> {
    List<Certifications> findByUserUserId(String userId);
}
