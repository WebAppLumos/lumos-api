package com.group4.lumos_api.user;

import com.group4.lumos_api.user.entity.Users;
import com.group4.lumos_api.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsersRepository usersRepository;

    @Override
    public void run(String... args) throws Exception {
        if (usersRepository.count() == 0) {
            Users testUser = Users.builder()
                    .userId("test-user-id")
                    .email("test@example.com")
                    .name("테스트 사용자")
                    .department("컴퓨터공학과")
                    .grade(3)
                    .studentNumber("2024001")
                    .build();
            usersRepository.save(testUser);
            System.out.println("Sample user data initialized: test-user-id");
        }
    }
}
