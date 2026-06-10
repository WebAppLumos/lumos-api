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

    private static String serviceAccountPath;

    private FirebaseConfig() {
    }

    public static void setServiceAccountPath(String path) {
        serviceAccountPath = path;
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

        String envPath = System.getenv("FIREBASE_SERVICE_ACCOUNT_PATH");
        if (envPath != null && !envPath.isBlank()) {
            return GoogleCredentials.fromStream(new FileInputStream(resolveFile(envPath)));
        }

        if (serviceAccountPath != null && !serviceAccountPath.isBlank()) {
            File file = resolveFile(serviceAccountPath);
            if (file.exists()) {
                return GoogleCredentials.fromStream(new FileInputStream(file));
            }
        }

        return GoogleCredentials.getApplicationDefault();
    }

    private static File resolveFile(String path) {
        File file = new File(path);
        if (file.isAbsolute()) {
            return file;
        }
        if (file.exists()) {
            return file;
        }
        return new File(System.getProperty("user.dir"), path);
    }
}
