package com.group4.lumos_api.student.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Students")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Students {
    @Id
    @Column(name = "studentID")
    private Long studentID;

    @Column(name = "name")
    private String name;
}
