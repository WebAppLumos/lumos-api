# Lumos API

A Spring Boot-based RESTful API project for managing student information, including certifications, language exams, and academic scores.

## Overview
- **Java 21**, **Spring Boot 4.0.6**
- Follows RESTful API standards using Spring MVC and Spring Data JPA.
- Database: **PostgreSQL** (Neon Cloud)

## Tech Stack
- **Framework:** Spring Boot 4.0.6 (Web, Data JPA, Validation)
- **Language:** Java 21
- **Database:** PostgreSQL
- **Tools:** Lombok, Spring Boot DevTools, Gradle

## Project Structure
The project is organized into functional modules:
- `user/`: Basic user information management (사용자 기본 정보 관리)
- `Certifications/`: User certification records (사용자별 자격증 취득 정보 관리)
- `Language_Exams/`: Official language exam scores (사용자별 공인외국어 시험 성적 관리)
- `previous_semester_scores/`: Academic scores from the previous semester (직전 학기 성적 관리)

### Directory Layout
```text
lumos-api/
├── src/main/java/com/group4/lumos_api/
│   ├── LumosApiApplication.java (Main Entry Point)
│   ├── Certifications/
│   ├── Language_Exams/
│   ├── user/
│   └── previous_semester_scores/
├── src/main/resources/
│   └── application.properties (Configuration)
├── build.gradle (Build & Dependencies)
└── README.md
```

## API Endpoints Summary

### User
- `GET    /api/users` - List all users
- `POST   /api/users` - Create a new user
- `PATCH  /api/users/{userId}` - Update user details

### Previous Semester Scores
- `GET    /api/users/{userId}/previous-semester-scores`
- `POST   /api/users/{userId}/previous-semester-scores`
- `PATCH  /api/previous-semester-scores/{scoreId}`

### Certifications
- `GET    /api/users/{userId}/certifications`
- `POST   /api/users/{userId}/certifications`
- `PATCH  /api/certifications/{certId}`

### Language Exams
- `GET    /api/users/{userId}/language-exams`
- `POST   /api/users/{userId}/language-exams`
- `PATCH  /api/language-exams/{examId}`

## Getting Started

### 1. Database Configuration
Database connection is configured in `src/main/resources/application.properties`.
```properties
spring.datasource.url=jdbc:postgresql://ep-autumn-mouse-ao51pabx-pooler.c-2.ap-southeast-1.aws.neon.tech:5432/lumos_db?sslmode=require
spring.datasource.username=lumos
spring.datasource.password=npg_owC70iYXpsWA
spring.jpa.hibernate.ddl-auto=update
```

### 2. Run the Application
```bash
# Grant execution permission (first time only)
chmod +x gradlew

# Run the server
./gradlew bootRun
```
The server will be available at `http://localhost:8080`.

## Contribution Rules
- **Branch Naming:** `feature/feature-name`, `bugfix/bug-name`
- **Commit Messages:** `feat: ...`, `fix: ...`, `docs: ...`
