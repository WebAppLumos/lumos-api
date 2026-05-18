# Lumos API

간단한 Spring Boot 기반 RESTful API 프로젝트입니다. 학기(Semester)와 수업(Course)을 관리하는 CRUD 엔드포인트를 제공합니다.

## 개요
- Java 21, Spring Boot 4.x 기반
- RESTful API 구조를 따르며 Spring MVC + Spring Data JPA로 구현되었습니다.

## 주요 기술 스택
- Java 21
- Spring Boot 4.x (Web, Data JPA, Validation)
- Spring MVC (REST)
- Spring Data JPA (Hibernate)
- PostgreSQL (JDBC 드라이버 포함)
- Lombok (보일러플레이트 감소)
- Jakarta Validation (`jakarta.validation`)

개발 편의:
- Spring Boot DevTools (개발 시 자동 리스타트)

## 주요 의존성 (build.gradle 기준)
- `org.springframework.boot:spring-boot-starter-web` 또는 `spring-boot-starter-webmvc`
- `org.springframework.boot:spring-boot-starter-data-jpa`
- `org.postgresql:postgresql`
- `org.projectlombok:lombok`
- `org.springframework.boot:spring-boot-starter-validation`

※ 참고: Flyway는 사용하지 않고 `spring.jpa.hibernate.ddl-auto=update`를 사용하도록 구성되어 있습니다.

## 프로젝트 구조(요약)
- `src/main/java/com/group4/lumos_api/semester` - 학기 관련 컨트롤러/서비스/레포/DTO
- `src/main/java/com/group4/lumos_api/course` - 수업 관련 컨트롤러/서비스/레포/DTO
- `src/main/resources/application.properties` - 데이터베이스 설정 및 JPA 옵션

## 주요 엔드포인트
- 학기
  - `POST /api/semesters` - 학기 생성
  - `GET  /api/semesters` - 학기 목록
  - `GET  /api/semesters/{id}` - 학기 상세
  - `PATCH /api/semesters/{id}` - 학기 수정
  - `DELETE /api/semesters/{id}` - 학기 삭제
- 수업 (학기 종속)
  - `POST /api/semesters/{semesterId}/courses` - 수업 생성
  - `GET  /api/semesters/{semesterId}/courses` - 학기별 수업 목록
  - `GET  /api/semesters/{semesterId}/courses/{courseId}` - 수업 상세
  - `PATCH /api/semesters/{semesterId}/courses/{courseId}` - 수업 수정
  - `DELETE /api/semesters/{semesterId}/courses/{courseId}` - 수업 삭제

## 빌드 및 실행
로컬에서 Gradle 래퍼 사용 권장:
```bash
./gradlew test
./gradlew bootRun
```

애플리케이션은 기본적으로 `localhost:8080`에서 실행됩니다. 데이터베이스 연결 정보는 `src/main/resources/application.properties`를 확인하세요.

## 테스트 및 검증
- 단위/통합 테스트: `./gradlew test` 실행
- 실제 요청 예시와 Postman 데이터는 각 API별 `README.md`를 읽고 그대로 따라가면 됩니다.
- 루트 `README.md`는 전체 구조와 실행 방법만 안내합니다.

## 주의사항
- 색상 등 입력 유효성은 DTO의 검증 어노테이션(`@NotBlank` 등)에 의존합니다. 추가 포맷 검증이 필요하면 `@Pattern`을 적용하세요.
- 문서의 엔드포인트와 코드 동작(특히 예외/HTTP 상태 코드)이 일치하는지 확인해 주세요.

## 기여 및 브랜치 규칙 (권장)
- 기능: `feat/<short-desc>` (예: `feat/course-search`)
- 버그: `fix/<short-desc>`
- 커밋 메시지: `type(scope): 한줄요약` (예: `feat(course): 수업 CRUD 추가`)

---
추가 설명이 필요하면 각 API별 `README.md`를 참고하세요.