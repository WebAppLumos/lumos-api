package com.group4.lumos_api.language_exams.entity;

import com.group4.lumos_api.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "language_exams")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LanguageExams {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exam_id")
    private Long examId;

    @Column(name = "exam_category")
    private String examCategory;

    @Column(name = "score")
    private String score;

    @Column(name = "exam_date")
    private LocalDate examDate;

    @Column(name = "year")
    private Integer year;

    @Column(name = "semester")
    private String semester;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;
}
