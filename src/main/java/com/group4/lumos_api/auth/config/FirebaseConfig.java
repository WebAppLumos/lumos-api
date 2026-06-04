package com.group4.lumos_api.auth.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FirebaseConfig {

    private FirebaseConfig() {
    }

    public static FirebaseApp initialize() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(loadCredentials())
                .build();

        return FirebaseApp.initializeApp(options);
    }

    private static GoogleCredentials loadCredentials() throws IOException {
        String serviceAccountJson = System.getenv("FIREBASE_SERVICE_ACCOUNT_JSON");
        if (serviceAccountJson != null && !serviceAccountJson.isBlank()) {
            return GoogleCredentials.fromStream(
                    new ByteArrayInputStream(serviceAccountJson.getBytes(StandardCharsets.UTF_8)));
        }

        String serviceAccountPath = System.getenv("FIREBASE_SERVICE_ACCOUNT_PATH");
        if (serviceAccountPath != null && !serviceAccountPath.isBlank()) {
            return GoogleCredentials.fromStream(new FileInputStream(serviceAccountPath));
        }

        return GoogleCredentials.getApplicationDefault();
    }
}
