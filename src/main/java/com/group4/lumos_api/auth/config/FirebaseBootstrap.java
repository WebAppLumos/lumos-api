package com.group4.lumos_api.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class FirebaseBootstrap {

    @Value("${firebase.service-account.path:}")
    private String serviceAccountPath;

    @PostConstruct
    public void init() {
        try {
            if (serviceAccountPath != null && !serviceAccountPath.isBlank()) {
                FirebaseConfig.setServiceAccountPath(serviceAccountPath);
            }
            FirebaseConfig.initialize();
            System.out.println("Firebase Admin SDK initialized successfully.");
        } catch (Exception e) {
            System.err.println("Firebase initialization failed: " + e.getMessage());
            // 개발 환경에서 Firebase 없이도 앱이 뜰 수 있도록 예외를 던지지 않고 로그만 남깁니다.
        }
    }
}
