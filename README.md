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
User (Student)
  ├─ Semester
  │   ├─ Course
  │   │   ├─ Note
  │   │   └─ Difficulty
  │   └─ Timetable
  │       └─ Entry -> Course
  ├─ Certifications
  ├─ Language Exams
  └─ Previous Semester Scores
```

- `user`: 시스템 사용자 (학생) 정보
- `semester`: 독립 실행 가능 (논리적으로 `user`에 귀속)
- `course`: `semester`에 종속
- `timetable`: `semester`에 종속
- `note`: `course`에 종속
- `difficulty`: `course`에 종속
- `entry`: `timetable`과 `course`에 종속
- `certifications`: 학생의 자격증 취득 정보
- `language_exams`: 학생의 어학 시험 성적 정보
- `previous_semester_scores`: 학생의 지난 학기 성적 정보

## 프로젝트 구조

```text
lumos-api/
├── src/main/java/com/group4/lumos_api/
│   ├── LumosApiApplication.java
│   ├── user/          # 사용자 관리
│   ├── semester/      # 학기 관리
│   ├── course/        # 수업 관리
│   ├── timetable/     # 시간표 관리
│   ├── entry/         # 수업 배치 관리
│   ├── note/          # 노트 관리
│   ├── difficulty/    # 난이도 관리
│   ├── Certifications/ # 자격증 관리
│   ├── Language_Exams/ # 어학 시험 관리
│   └── previous_semester_scores/ # 지난 학기 성적 관리
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
| User | [user/README.md](src/main/java/com/group4/lumos_api/user/README.md) | 사용자(학생) 생성, 조회, 수정, 삭제 |
| Semester | [semester/README.md](src/main/java/com/group4/lumos_api/semester/README.md) | 학기 생성, 목록 조회, 상세 조회, 수정, 삭제 |
| Course | [course/README.md](src/main/java/com/group4/lumos_api/course/README.md) | 학기에 종속된 수업 생성, 조회, 수정, 삭제 |
| Timetable | [timetable/README.md](src/main/java/com/group4/lumos_api/timetable/README.md) | 학기에 종속된 시간표 생성, 조회, 수정, 삭제 |
| Entry | [entry/README.md](src/main/java/com/group4/lumos_api/entry/README.md) | 시간표에 수업 배치, 배치 목록 조회, 삭제 |
| Note | [note/README.md](src/main/java/com/group4/lumos_api/note/README.md) | 수업별 노트 생성, 조회, 검색, 수정, 삭제, 고정 |
| Difficulty | [difficulty/README.md](src/main/java/com/group4/lumos_api/difficulty/README.md) | 수업 난이도 설정/조회, 시간표 평균 난이도 조회 |
| Certifications | [Certifications/README.md](src/main/java/com/group4/lumos_api/Certifications/README.md) | 학생별 자격증 취득 정보 관리 |
| Language Exams | [Language_Exams/README.md](src/main/java/com/group4/lumos_api/Language_Exams/README.md) | 어학 시험(TOEIC 등) 성적 관리 |

## 주요 엔드포인트

### User

| 기능 | 메서드 | 엔드포인트 |
|---|---|---|
| 사용자 생성 | POST | `/api/users` |
| 사용자 목록 조회 | GET | `/api/users` |
| 사용자 상세 조회 | GET | `/api/users/{userId}` |
| 사용자 수정 | PATCH | `/api/users/{userId}` |
| 사용자 삭제 | DELETE | `/api/users/{userId}` |

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

### Certifications

| 기능 | 메서드 | 엔드포인트 |
|---|---|---|
| 자격증 등록 | POST | `/api/certifications` |
| 자격증 목록 조회 | GET | `/api/certifications/student/{studentId}` |
| 자격증 수정 | PUT | `/api/certifications/{certId}` |
| 자격증 삭제 | DELETE | `/api/certifications/{certId}` |

### Language Exams

| 기능 | 메서드 | 엔드포인트 |
|---|---|---|
| 성적 등록 | POST | `/api/language-exams` |
| 성적 목록 조회 | GET | `/api/language-exams/student/{studentId}` |
| 성적 수정 | PUT | `/api/language-exams/{examId}` |
| 성적 삭제 | DELETE | `/api/language-exams/{examId}` |

### Previous Semester Scores

| 기능 | 메서드 | 엔드포인트 |
|---|---|---|
| 성적 등록 | POST | `/api/users/{userId}/previous-semester-scores` |
| 성적 목록 조회 | GET | `/api/users/{userId}/previous-semester-scores` |
| 성적 수정 | PATCH | `/api/previous-semester-scores/{scoreId}` |
| 성적 삭제 | DELETE | `/api/previous-semester-scores/{scoreId}` |

## 데이터베이스

PostgreSQL을 사용합니다. JPA 엔티티는 ERD의 물리 테이블명과 컬럼명을 기준으로 매핑되어 있습니다.

| 테이블 | 설명 |
|---|---|
| `users` | 사용자(학생) |
| `semester` | 학기 |
| `course` | 수업 |
| `timetable` | 시간표 |
| `entry` | 시간표 수업 배치 |
| `note` | 수업 노트 |
| `difficulty` | 수업 난이도 |
| `certifications` | 자격증 정보 |
| `language_exams` | 어학 시험 성적 |
| `previous_semester_scores` | 지난 학기 성적 |

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
