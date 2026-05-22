package com.group4.lumos_api.student;

import com.group4.lumos_api.student.entity.Students;
import com.group4.lumos_api.student.repository.StudentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final StudentsRepository studentsRepository;

    @Override
    public void run(String... args) throws Exception {
        if (studentsRepository.count() == 0) {
            Students testStudent = Students.builder()
                    .studentID(1L)
                    .name("테스트 학생")
                    .build();
            studentsRepository.save(testStudent);
            System.out.println("Sample student data initialized: ID = 1");
        }
    }
}
