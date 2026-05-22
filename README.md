# Lumos API

간단한 Spring Boot 기반 RESTful API 프로젝트입니다. 자격증(Certification) 정보를 관리하는 CRUD 엔드포인트를 제공합니다.

## 개요
- Java 21, Spring Boot 4.x 기반
- RESTful API 구조를 따르며 Spring MVC + Spring Data JPA로 구현되었습니다.

## 주요 기술 스택
- **Java 21**
- **Spring Boot 4.x** (Web, Data JPA, Validation)
- **Spring MVC** (REST API 구현)
- **Spring Data JPA** (Hibernate를 통한 DB 매핑)
- **PostgreSQL** (Neon Cloud DB 연동)
- **Lombok** (코드 간소화)
- **Jakarta Validation** (데이터 유효성 검증)

개발 편의:
- **Spring Boot DevTools** (실시간 코드 변경 반영)

## 주요 의존성 (build.gradle)
- `spring-boot-starter-webmvc`
- `spring-boot-starter-data-jpa`
- `postgresql`
- `lombok`
- `spring-boot-starter-validation`

※ 본 프로젝트는 Flyway 대신 `spring.jpa.hibernate.ddl-auto=update` 설정을 통해 스키마를 자동 관리합니다.

## 프로젝트 구조 (Package Structure)
- `Certifications/`: 사용자별 자격증 취득 정보 관리 기능
- `Language_Exams/`: 사용자별 공인외국어 시험 성적 관리 기능
- `user/`: 사용자 기본 정보 관리 (기존 student 모듈에서 확장 및 변경)
- `previous_semester_scores/`: 직전 학기 성적 관리 기능

## 상세 디렉토리 구조
```text
lumos-api/
├── src/main/java/com/group4/lumos_api/
│   ├── LumosApiApplication.java (메인 진입점)
│   ├── Certifications/ (자격증 관리)
│   ├── Language_Exams/ (공인외국어 성적 관리)
│   ├── user/           (사용자 기본 정보 관리)
│   ├── previous_semester_scores/ (직전 학기 성적 관리)
│   └── (기타 각 기능별 동일한 구조 적용)
├── src/main/resources/
│   └── application.properties (PostgreSQL 및 JPA 설정)
├── build.gradle (빌드 및 의존성 설정)
└── README.md (현재 파일)
```

## 주요 엔드포인트 및 데이터 모델 요약

### 사용자 (User)
- `GET    /api/users`
- `POST   /api/users`
- `PATCH  /api/users/{userId}`

**데이터 모델 (JSON):**
```json
{
  "userId": "test-user-id",
  "email": "user@example.com",
  "name": "홍길동",
  "phoneNumber": "010-1234-5678",
  "department": "컴퓨터공학과",
  "grade": 3,
  "studentNumber": "2024001"
}
```

### 직전 학기 성적 (Previous Semester Scores)
- `GET    /api/users/{userId}/previous-semester-scores`
- `POST   /api/users/{userId}/previous-semester-scores`
- `PATCH  /api/previous-semester-scores/{scoreId}`

**데이터 모델 (JSON):**
```json
{
  "score": 4.25,
  "year": "2024-03-01",
  "semester": "1학기"
}
```

### 자격증 (Certification)
- `GET    /api/users/{userId}/certifications`
- `POST   /api/users/{userId}/certifications`
- `PATCH  /api/certifications/{certId}`

**데이터 모델 (JSON):**
```json
{
  "certName": "정보처리기사",
  "issueDate": "2024-05-20"
}
```

### 공인외국어 성적 (Language Exams)
- `GET    /api/users/{userId}/language-exams`
- `POST   /api/users/{userId}/language-exams`
- `PATCH  /api/language-exams/{examId}`

**데이터 모델 (JSON):**
```json
{
  "examCategory": "TOEIC",
  "score": "950",
  "examDate": "2024-04-15",
  "year": 2024,
  "semester": "1학기",
  "expiryDate": "2026-04-15"
}
```




## 빌드 및 실행 방법

### 1. 데이터베이스 설정
`src/main/resources/application.properties`에 아래와 같이 PostgreSQL 연결 정보가 설정되어 있습니다.
```properties
spring.datasource.url=jdbc:postgresql://ep-autumn-mouse-ao51pabx-pooler.c-2.ap-southeast-1.aws.neon.tech:5432/lumos_db?sslmode=require
spring.datasource.username=lumos
spring.datasource.password=npg_owC70iYXpsWA
spring.jpa.hibernate.ddl-auto=update
```

### 2. 프로젝트 실행
```bash
# 권한 부여 (최초 1회)
chmod +x gradlew

# 서버 실행
./gradlew bootRun
```
서버는 기본적으로 `http://localhost:8080`에서 구동됩니다.

## 테스트 가이드
- 각 기능 패키지 내의 `README.md` 파일에 Postman 요청 예시와 데이터 모델이 상세히 기술되어 있습니다.
- `DataInitializer` 클래스를 통해 서버 기동 시 테스트용 샘플 데이터(학생 ID: 1)가 자동으로 생성됩니다.

## 기여 및 커밋 규칙
- **브랜치**: `feature/기능명`, `bugfix/버그명`
- **커밋 메시지**: `feat: 기능 추가`, `fix: 버그 수정`, `docs: 문서 수정`
