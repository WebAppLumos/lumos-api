package com.group4.lumos_api.auth.config;

import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.IOException;

@Configuration
public class FirebaseBootstrap {

    @PostConstruct
    public void init() throws IOException {
        FirebaseConfig.initialize();
    }
}
