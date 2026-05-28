# Lumos API

Lumos의 학기, 수업, 시간표, 노트, 난이도 정보를 관리하는 Spring Boot 기반 REST API입니다.

## 개요

이 API는 학기를 최상위 기준으로 두고, 학기에 종속된 수업과 시간표를 관리합니다. 노트와 난이도는 수업에 종속되며, 수업 배치(Entry)는 시간표와 수업을 연결합니다.

## 기술 스택

- Java 21
- Spring Boot 4.x
- Spring MVC
- Spring Data JPA
- Jakarta Validation
- PostgreSQL
- Lombok
- Gradle Wrapper

## 도메인 관계

```text
Semester
  ├─ Course
  │   ├─ Note
  │   └─ Difficulty
  └─ Timetable
      └─ Entry -> Course
```

- `semester`: 독립 실행 가능
- `course`: `semester`에 종속
- `timetable`: `semester`에 종속
- `note`: `course`에 종속
- `difficulty`: `course`에 종속
- `entry`: `timetable`과 `course`에 종속

## 프로젝트 구조

```text
lumos-api/
├── src/main/java/com/group4/lumos_api/
│   ├── LumosApiApplication.java
│   ├── semester/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── repository/
│   │   ├── service/
│   │   └── README.md
│   ├── course/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── repository/
│   │   ├── service/
│   │   └── README.md
│   ├── timetable/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── repository/
│   │   ├── service/
│   │   └── README.md
│   ├── entry/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── repository/
│   │   ├── service/
│   │   └── README.md
│   ├── note/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── repository/
│   │   ├── service/
│   │   └── README.md
│   └── difficulty/
│       ├── controller/
│       ├── dto/
│       ├── entity/
│       ├── repository/
│       ├── service/
│       └── README.md
├── src/main/resources/
│   └── application.properties
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
└── README.md
```

## API 문서

각 API별 상세 요청/응답 예시와 Postman 테스트 시나리오는 도메인별 README를 참고하세요.

| 도메인 | 문서 | 설명 |
|---|---|---|
| Semester | [semester/README.md](src/main/java/com/group4/lumos_api/semester/README.md) | 학기 생성, 목록 조회, 상세 조회, 수정, 삭제 |
| Course | [course/README.md](src/main/java/com/group4/lumos_api/course/README.md) | 학기에 종속된 수업 생성, 조회, 수정, 삭제 |
| Timetable | [timetable/README.md](src/main/java/com/group4/lumos_api/timetable/README.md) | 학기에 종속된 시간표 생성, 조회, 수정, 삭제 |
| Entry | [entry/README.md](src/main/java/com/group4/lumos_api/entry/README.md) | 시간표에 수업 배치, 배치 목록 조회, 삭제 |
| Note | [note/README.md](src/main/java/com/group4/lumos_api/note/README.md) | 수업별 노트 생성, 조회, 검색, 수정, 삭제, 고정 |
| Difficulty | [difficulty/README.md](src/main/java/com/group4/lumos_api/difficulty/README.md) | 수업 난이도 설정/조회, 시간표 평균 난이도 조회 |

## 주요 엔드포인트

### Semester

| 기능 | 메서드 | 엔드포인트 |
|---|---|---|
| 학기 생성 | POST | `/api/semesters` |
| 학기 목록 조회 | GET | `/api/semesters` |
| 학기 상세 조회 | GET | `/api/semesters/{semesterId}` |
| 학기 수정 | PATCH | `/api/semesters/{semesterId}` |
| 학기 삭제 | DELETE | `/api/semesters/{semesterId}` |

### Course

| 기능 | 메서드 | 엔드포인트 |
|---|---|---|
| 수업 생성 | POST | `/api/semesters/{semesterId}/courses` |
| 수업 목록 조회 | GET | `/api/semesters/{semesterId}/courses` |
| 수업 상세 조회 | GET | `/api/semesters/{semesterId}/courses/{courseId}` |
| 수업 수정 | PATCH | `/api/semesters/{semesterId}/courses/{courseId}` |
| 수업 삭제 | DELETE | `/api/semesters/{semesterId}/courses/{courseId}` |

### Timetable

| 기능 | 메서드 | 엔드포인트 |
|---|---|---|
| 시간표 생성 | POST | `/api/semesters/{semesterId}/timetables` |
| 시간표 목록 조회 | GET | `/api/semesters/{semesterId}/timetables` |
| 시간표 상세 조회 | GET | `/api/semesters/{semesterId}/timetables/{timetableId}` |
| 시간표 수정 | PATCH | `/api/semesters/{semesterId}/timetables/{timetableId}` |
| 시간표 삭제 | DELETE | `/api/semesters/{semesterId}/timetables/{timetableId}` |

### Entry

| 기능 | 메서드 | 엔드포인트 |
|---|---|---|
| 수업 배치 | POST | `/api/semesters/{semesterId}/timetables/{timetableId}/entries` |
| 수업 배치 목록 조회 | GET | `/api/semesters/{semesterId}/timetables/{timetableId}/entries` |
| 수업 배치 삭제 | DELETE | `/api/semesters/{semesterId}/timetables/{timetableId}/entries/{entryId}` |

### Note

| 기능 | 메서드 | 엔드포인트 |
|---|---|---|
| 노트 생성 | POST | `/api/semesters/{semesterId}/courses/{courseId}/notes` |
| 노트 목록 조회 | GET | `/api/semesters/{semesterId}/courses/{courseId}/notes` |
| 노트 검색 | GET | `/api/semesters/{semesterId}/courses/{courseId}/notes?q={keyword}` |
| 노트 상세 조회 | GET | `/api/semesters/{semesterId}/courses/{courseId}/notes/{noteId}` |
| 노트 수정 | PATCH | `/api/semesters/{semesterId}/courses/{courseId}/notes/{noteId}` |
| 노트 삭제 | DELETE | `/api/semesters/{semesterId}/courses/{courseId}/notes/{noteId}` |
| 노트 고정 설정/해제 | PATCH | `/api/semesters/{semesterId}/courses/{courseId}/notes/{noteId}/pin` |

### Difficulty

| 기능 | 메서드 | 엔드포인트 |
|---|---|---|
| 난이도 설정 | POST | `/api/semesters/{semesterId}/courses/{courseId}/difficulty` |
| 난이도 조회 | GET | `/api/semesters/{semesterId}/courses/{courseId}/difficulty` |
| 시간표 평균 난이도 조회 | GET | `/api/semesters/{semesterId}/timetables/{timetableId}/difficulty` |

## 데이터베이스

PostgreSQL을 사용합니다. JPA 엔티티는 ERD의 물리 테이블명과 컬럼명을 기준으로 매핑되어 있습니다.

| 테이블 | 설명 |
|---|---|
| `semester` | 학기 |
| `course` | 수업 |
| `timetable` | 시간표 |
| `entry` | 시간표 수업 배치 |
| `note` | 수업 노트 |
| `difficulty` | 수업 난이도 |

현재 설정은 `spring.jpa.hibernate.ddl-auto=update`를 사용합니다.

## 실행 방법

### 사전 요구사항

- JDK 21 이상
- PostgreSQL 또는 Neon 같은 PostgreSQL 호환 DB

### 데이터베이스 설정

`src/main/resources/application.properties`에서 DB 연결 정보를 설정합니다.

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/lumos_db
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
```

### 빌드

Windows PowerShell:

```bash
.\gradlew.bat build
```

macOS/Linux:

```bash
./gradlew build
```

### 실행

Windows PowerShell:

```bash
.\gradlew.bat bootRun
```

macOS/Linux:

```bash
./gradlew bootRun
```

기본 실행 주소는 `http://localhost:8080`입니다.

## Postman 테스트

전체 API 흐름을 한 번에 테스트할 수 있는 Postman 컬렉션이 준비되어 있습니다.

[docs/postman/lumos-api.postman_collection.json](../docs/postman/lumos-api.postman_collection.json)

기본 컬렉션 변수:

```text
baseUrl=http://localhost:8080
```

권장 실행 순서:

```text
Semester -> Course -> Timetable -> Entry -> Note -> Difficulty
```

## 검증

최근 확인한 빌드 명령:

```bash
.\gradlew.bat build
```

결과:

```text
BUILD SUCCESSFUL
```

## 브랜치 및 커밋 규칙

권장 브랜치명:

```text
feature/기능명
docs/문서명
bugfix/버그명
refactor/대상명
```

권장 커밋 메시지:

```text
feat(domain): 기능 요약
docs(domain): 문서 요약
fix(domain): 버그 수정 요약
refactor(domain): 리팩터링 요약
```

예시:

```text
feat(note): 수업별 노트 관리 API 구현
docs(note): 노트 API 사용 문서 추가
```
