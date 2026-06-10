# 📅 Lumos API - Calendar & To-Do Module

이 모듈은 파이어베이스 인증(Firebase Auth) 기반의 학사 일정(공통) 및 학생 개인의 To-Do 일정을 통합 관리하는 기능을 제공합니다.

---

## 🚀 시작하기 (How to Run)

터미널에서 프로젝트 루트 폴더로 이동한 뒤 아래 명령어를 입력하여 서버를 실행합니다:

# Windows (PowerShell)
.\gradlew bootRun

# macOS / Linux
./gradlew bootRun

서버가 실행되면 http://localhost:8080에서 API를 호출할 수 있습니다.

---

## 📂 패키지 구조

com.group4.lumos_api.calendar/
├── controller/
│   └── CalendarEventController    # REST API 엔드포인트 처리 (CORS 완료)
├── service/
│   └── CalendarEventService       # 비즈니스 로직 및 admin 학사일정 방어벽
├── repository/
│   └── CalendarEventRepository    # DB 접근 및 Native Query 복합 필터
├── entity/
│   └── CalendarEvent              # DB 엔티티 (user_id 매핑 완료)
└── dto/
    ├── CalendarEventRequest       # 요청 데이터 규격
    └── CalendarEventResponse      # 응답 데이터 규격

---

## 🚀 API 명세 (Endpoints)

| 기능명 | 엔드포인트 | Method | 설명 |
| :--- | :--- | :--- | :--- |
| 일정 통합 조회/검색 | /api/calendar/events | GET | 필터(date, keyword, category, priority) 기반 복합 조회 |
| 일정 상세 조회 | /api/calendar/events/{scheduleId} | GET | 특정 일정 1개 상세 조회 |
| 개인 일정 등록 | /api/calendar/events | POST | 새로운 개인 일정 생성 |
| 일정 내용 수정 | /api/calendar/events/{scheduleId} | PATCH | 기존 일정 정보 수정 |
| 완료 상태 토글 | /api/calendar/events/{scheduleId}/toggle | PATCH | 완료/미완료 상태 토글 |
| 일정 삭제 | /api/calendar/events/{scheduleId} | DELETE | 일정 삭제 (학사 일정은 삭제 불가) |

---

## 🔒 데이터 정책 및 보안 격리

- 학사 일정 (user_id = 'admin'): 시스템 공통 일정으로 보호됩니다. 일반 학생 계정으로 수정/삭제 시 백엔드에서 원천 차단됩니다.
- 개인 일정: 고유 UID를 기반으로 매핑되며, 등록한 본인만 모든 권한(CRUD)을 가집니다.
- 조회 메커니즘: API 호출 시 로그인한 사용자의 정보를 바탕으로 [개인 일정 + 전체 학사 일정]을 자동으로 결합하여 반환합니다.

---

## 🎨 데이터 상세 (Enum & Fields)

### 1. 필드 설명
| 필드명 | 타입 | 필수여부 | 설명 |
| :--- | :--- | :--- | :--- |
| scheduleId | Long | 자동생성 | 일정 고유 ID |
| userId | String | 필수 | 파이어베이스 고유 UID |
| title | String | 필수 | 일정 제목 |
| content | String | 선택 | 일정 상세 내용 |
| date | LocalDate | 필수 | 일정 날짜 (YYYY-MM-DD) |
| isCompleted | boolean | 선택 | 완료 여부 (기본값: false) |
| category | String | 선택 | 분류 (STUDY, WORK, PRIVATE, ACADEMIC, OTHER) |
| priority | String | 선택 | 중요도 (HIGH, MEDIUM, LOW) |

---

## 🧪 테스트 시나리오 (Test Flow)

Postman 등을 활용해 아래 순서대로 호출하며 기능을 검증하십시오.

### 1단계: 개인 일정 생성 (POST)
- Endpoint: POST /api/calendar/events
- Body: {"title": "알고리즘 공부", "content": "도서관", "date": "2026-06-15", "category": "STUDY", "priority": "HIGH"}

### 2단계: 일정 통합 조회 및 검색/필터 기능 검증 (GET)
- **전체 조회**: GET /api/calendar/events
- **키워드 검색**: GET /api/calendar/events?keyword=알고리즘
- **카테고리 필터**: GET /api/calendar/events?category=STUDY
- **날짜 필터**: GET /api/calendar/events?date=2026-06-15
- **복합 필터링**: GET /api/calendar/events?category=STUDY&priority=HIGH&keyword=알고리즘

### 3단계: 완료 상태 토글 (PATCH - Toggle)
- Endpoint: PATCH /api/calendar/events/{scheduleId}/toggle

### 4단계: 일정 내용 수정 (PATCH)
- Endpoint: PATCH /api/calendar/events/{scheduleId}
- Body: {"content": "장소를 카페로 변경", "priority": "MEDIUM"}

### 5단계: 일정 삭제 (DELETE)
- Endpoint: DELETE /api/calendar/events/{scheduleId}

---

## ⚠️ 주의사항

1. Enum 대소문자 규격: category, priority는 반드시 대문자(예: STUDY)여야 합니다.
2. 학사 일정 보호: 'admin'으로 등록된 일정은 수정/삭제 요청 시 403 Forbidden 에러가 발생합니다.

---

## 💡 Frontend (React) 통합 가이드

1. 로그인 연동: 파이어베이스 로그인 후 획득한 UID를 모든 요청의 헤더(X-User-Id)에 포함하여 전송하십시오.
2. UI 권한 구분: 응답 JSON의 userId가 'admin'인 경우, 프론트엔드에서 수정/삭제 버튼을 숨기고 공통 공지로 렌더링하십시오.