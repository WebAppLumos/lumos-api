package com.group4.lumos_api.auth.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class FirebaseBootstrap {

    @Value("${firebase.service-account.path:}")
    private String serviceAccountPath;

    @PostConstruct
    public void init() throws IOException {
        if (serviceAccountPath != null && !serviceAccountPath.isBlank()) {
            FirebaseConfig.setServiceAccountPath(serviceAccountPath);
        }
        FirebaseConfig.initialize();
    }
}