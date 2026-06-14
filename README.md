# Lumos API

Lumos의 학기, 수업, 시간표, 노트 정보를 관리하는 Spring Boot 기반 REST API입니다.

## 개요

이 API는 학기를 최상위 기준으로 두고, 학기에 종속된 수업과 시간표를 관리합니다. 노트는 수업에 종속되며, 수업 배치(Entry)는 시간표와 수업을 연결합니다. 수업 난이도는 별도 도메인이 아니라 수업(Course)의 속성(`difficultyLevel`)으로, 수업 생성/수정으로 설정하고 수업 조회 응답에 포함됩니다.

### URL 규칙

컬렉션(생성/목록)은 상위 리소스 아래에 중첩하고, 특정 단건(상세/수정/삭제)은 최상위 경로로 평탄화합니다.

- 예) 생성·목록: `POST /api/semesters/{semesterId}/courses`, 단건: `GET /api/courses/{courseId}`
- 예) 생성·목록: `POST /api/courses/{courseId}/notes`, 단건: `GET /api/notes/{noteId}`

## 기술 스택

- Java 21
- Spring Boot 4.x
- Spring MVC
- Spring Security (Firebase ID 토큰 기반 인증)
- Spring Data JPA
- Jakarta Validation
- PostgreSQL
- Lombok
- Gradle Wrapper

## 인증

`/api/auth/**`를 제외한 모든 엔드포인트는 인증이 필요합니다. 클라이언트는 Firebase 클라이언트 SDK로 발급받은 **ID 토큰**을 모든 요청 헤더에 담아 보냅니다.

```http
Authorization: Bearer <Firebase ID Token>
```

- 서버는 매 요청마다 ID 토큰을 검증하고, 토큰의 사용자(uid)를 기준으로 **본인 소유 데이터만** 접근하도록 강제합니다(학기·수업·시간표·노트·배치).
- 서버는 별도의 액세스 토큰을 발급하지 않습니다. ID 토큰이 만료(기본 1시간)되면 Firebase 클라이언트 SDK가 자동으로 새 ID 토큰을 갱신하며, 클라이언트는 갱신된 ID 토큰을 그대로 사용합니다.
- 토큰이 없거나 유효하지 않으면 `401 Unauthorized`, 타인 소유 리소스 접근 시 `404 Not Found`로 응답합니다.

클라이언트 인증 흐름:

```text
1. Firebase 클라이언트 SDK 로그인 → ID 토큰 획득
2. POST /api/auth/login  { idToken, (프로필) }   → 사용자 등록/동기화
3. 이후 모든 요청에 Authorization: Bearer <ID 토큰>
4. ID 토큰 만료 시 SDK가 자동 갱신 → 새 ID 토큰으로 교체
```

## 도메인 관계

```text
User (Student)
  ├─ Semester
  │   ├─ Course  (difficultyLevel 속성 포함)
  │   │   └─ Note
  │   └─ Timetable
  │       └─ Entry -> Course
  ├─ Certifications
  ├─ Language Exams
  └─ Previous Semester Scores
```

- `user`: 시스템 사용자 (학생) 정보
- `semester`: 독립 실행 가능 (논리적으로 `user`에 귀속)
- `course`: `semester`에 종속 (난이도 `difficultyLevel`을 속성으로 보유)
- `timetable`: `semester`에 종속
- `note`: `course`에 종속
- `entry`: `timetable`과 `course`에 종속
- `certifications`: 학생의 자격증 취득 정보
- `language_exams`: 학생의 어학 시험 성적 정보
- `previous_semester_scores`: 학생의 지난 학기 성적 정보

## 프로젝트 구조

```text
lumos-api/
├── src/main/java/com/group4/lumos_api/
│   ├── LumosApiApplication.java
│   ├── user/                  # 사용자 관리
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── repository/
│   │   ├── service/
│   │   └── README.md
│   ├── semester/              # 학기 관리
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── repository/
│   │   ├── service/
│   │   └── README.md
│   ├── course/                # 수업 관리
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── repository/
│   │   ├── service/
│   │   └── README.md
│   ├── timetable/             # 시간표 관리
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── repository/
│   │   ├── service/
│   │   └── README.md
│   ├── entry/                 # 수업 배치 관리
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── repository/
│   │   ├── service/
│   │   └── README.md
│   ├── note/                  # 노트 관리
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── repository/
│   │   ├── service/
│   │   └── README.md
├── calendar/              # 캘린더 및 To-Do 관리
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── repository/
│   │   ├── service/
│   │   └── README.md
│   ├── certifications/         # 자격증 관리
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── repository/
│   │   ├── service/
│   │   └── README.md
│   ├── Language_Exams/         # 어학 시험 관리
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── repository/
│   │   ├── service/
│   │   └── README.md
│   └── previous_semester_scores/ # 지난 학기 성적 관리
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
| Auth | [auth/README.md](src/main/java/com/group4/lumos_api/auth/README.md) | Firebase ID 토큰 검증, 로그인(등록/동기화), 로그아웃, 토큰 재검증(세션 확인) |
| User | [user/README.md](src/main/java/com/group4/lumos_api/user/README.md) | 내 정보(`/me`) 조회, 수정, 탈퇴 |
| Semester | [semester/README.md](src/main/java/com/group4/lumos_api/semester/README.md) | 학기 생성, 목록 조회, 상세 조회, 수정, 삭제 |
| Course | [course/README.md](src/main/java/com/group4/lumos_api/course/README.md) | 학기에 종속된 수업 생성, 조회, 수정, 삭제 |
| Timetable | [timetable/README.md](src/main/java/com/group4/lumos_api/timetable/README.md) | 학기에 종속된 시간표 생성, 조회, 수정, 삭제 |
| Entry | [entry/README.md](src/main/java/com/group4/lumos_api/entry/README.md) | 시간표에 수업 배치, 배치 목록 조회, 수정, 삭제 |
| Note | [note/README.md](src/main/java/com/group4/lumos_api/note/README.md) | 수업별 노트 생성, 조회, 검색, 수정, 삭제, 고정 |
| Certifications | [certifications/README.md](src/main/java/com/group4/lumos_api/certifications/README.md) | 학생별 자격증 취득 정보 관리 |
| Language Exams | [Language_Exams/README.md](src/main/java/com/group4/lumos_api/Language_Exams/README.md) | 어학 시험(TOEIC 등) 성적 관리 |

## 주요 엔드포인트

> `/api/auth/**`를 제외한 모든 엔드포인트는 `Authorization: Bearer <Firebase ID Token>` 헤더가 필요합니다.

### Auth (인증 불필요)

| 기능 | 도메인 | 엔드포인트 | 메서드 | 설명 |
|---|---|---|---|---|
| 로그인 | auth | `/api/auth/login` | POST | Firebase ID 토큰 검증 후 회원 등록/로그인 |
| 로그아웃 | auth | `/api/auth/logout` | POST | Firebase refresh token 폐기 및 로그아웃 처리 |
| 토큰 검증 | auth | `/api/auth/refresh` | POST | Firebase ID 토큰 재검증 및 사용자 프로필 반환 (세션 유효성 확인) |

### User

| 기능 | 메서드 | 엔드포인트 |
|---|---|---|
| 사용자 등록(관리/직접) | POST | `/api/users` |
| 전체 사용자 조회(관리자) | GET | `/api/users` |
| 내 정보 조회 | GET | `/api/users/me` |
| 내 정보 수정 | PATCH | `/api/users/me` |
| 프로필 이미지 수정 | PATCH | `/api/users/me/profile-image` |
| 알림 설정 조회(미구현) | GET | `/api/users/me/settings` |
| 알림 설정 수정(미구현) | PATCH | `/api/users/me/settings` |
| 회원 탈퇴 | DELETE | `/api/users/me` |

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
| 수업 상세 조회 | GET | `/api/courses/{courseId}` |
| 수업 수정 | PATCH | `/api/courses/{courseId}` |
| 수업 삭제 | DELETE | `/api/courses/{courseId}` |

> 수업 난이도(`difficultyLevel`, 1~5)는 수업 생성/수정 요청 본문으로 설정하고, 수업 조회 응답에 포함됩니다. 별도 난이도 엔드포인트는 없습니다.

### Timetable

| 기능 | 메서드 | 엔드포인트 |
|---|---|---|
| 시간표 생성 | POST | `/api/semesters/{semesterId}/timetables` |
| 시간표 목록 조회 | GET | `/api/semesters/{semesterId}/timetables` |
| 시간표 상세 조회 | GET | `/api/timetables/{timetableId}` |
| 시간표 수정 | PATCH | `/api/timetables/{timetableId}` |
| 시간표 삭제 | DELETE | `/api/timetables/{timetableId}` |

### Entry

| 기능 | 메서드 | 엔드포인트 |
|---|---|---|
| 수업 배치 | POST | `/api/timetables/{timetableId}/entries` |
| 수업 배치 목록 조회 | GET | `/api/timetables/{timetableId}/entries` |
| 수업 배치 수정 | PATCH | `/api/entries/{entryId}` |
| 수업 배치 삭제 | DELETE | `/api/entries/{entryId}` |

> 수업 배치 요청 본문: `courseId`, `dayOfWeek`(1~7), `startTime`, `endTime` (모두 필수). 같은 시간표의 동일 요일에 시간이 겹치는 배치는 거부됩니다.

### Note

| 기능 | 메서드 | 엔드포인트 |
|---|---|---|
| 노트 생성 | POST | `/api/courses/{courseId}/notes` |
| 노트 목록 조회 | GET | `/api/courses/{courseId}/notes` |
| 노트 검색 | GET | `/api/courses/{courseId}/notes?q={keyword}` |
| 노트 상세 조회 | GET | `/api/notes/{noteId}` |
| 노트 수정 | PATCH | `/api/notes/{noteId}` |
| 노트 삭제 | DELETE | `/api/notes/{noteId}` |
| 노트 고정 설정/해제 | PATCH | `/api/notes/{noteId}/pin` |

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
| `semesters` | 학기 |
| `courses` | 수업 (난이도 `difficulty_level` 컬럼 포함) |
| `timetables` | 시간표 |
| `entries` | 시간표 수업 배치 |
| `notes` | 수업 노트 |
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
idToken=<Firebase ID Token>
```

컬렉션은 Bearer 인증(`{{idToken}}`)이 설정되어 있어 모든 요청에 `Authorization: Bearer {{idToken}}`가 자동으로 적용됩니다. 테스트 전에 `idToken` 변수에 유효한 Firebase ID 토큰을 넣어야 합니다.

권장 실행 순서:

```text
Semester -> Course -> Timetable -> Entry -> Note
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
