package com.group4.lumos_api.auth.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FirebaseConfig {

    private static final String LOCAL_SERVICE_ACCOUNT_FILE = "firebase-service-account.json";

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

        File localFile = resolveLocalServiceAccountFile();
        if (localFile.exists()) {
            return GoogleCredentials.fromStream(new FileInputStream(localFile));
        }

        return GoogleCredentials.getApplicationDefault();
    }

    private static File resolveLocalServiceAccountFile() {
        File file = new File(LOCAL_SERVICE_ACCOUNT_FILE);
        if (file.isAbsolute() || file.exists()) {
            return file;
        }
        return new File(System.getProperty("user.dir"), LOCAL_SERVICE_ACCOUNT_FILE);
    }
}
