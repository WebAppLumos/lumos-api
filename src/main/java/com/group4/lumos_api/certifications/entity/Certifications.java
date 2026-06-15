package com.group4.lumos_api.certifications.entity;

import com.group4.lumos_api.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "Certifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Certifications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cert_id")
    private Long certId;

    @Column(name = "cert_name")
    private String certName;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;
}
