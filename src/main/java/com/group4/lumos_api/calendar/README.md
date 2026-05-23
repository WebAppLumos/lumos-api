# 📅 Lumos API - Calendar & To-Do Module

이 모듈은 학사 일정(공통) 및 학생 개인의 To-Do 일정을 통합 관리하는 기능을 제공합니다.

## 📂 패키지 구조

```text
com.group4.lumos_api.calendar/
├── controller/
│   └── CalendarEventController    # REST API 엔드포인트 처리
├── service/
│   └── CalendarEventService       # 비즈니스 로직 및 권한 검증
├── repository/
│   └── CalendarEventRepository    # DB 접근 및 복합 필터 쿼리
├── entity/
│   └── CalendarEvent              # DB 엔티티 (isCompleted, Category 등 포함)
├── dto/
│   ├── CalendarEventRequest       # 요청 데이터 규격
│   └── CalendarEventResponse      # 응답 데이터 규격
└── CalendarDataInitializer.java   # 초기 테스트 데이터 생성기
```

---

## 🚀 API 명세 (Endpoints)

### 📌 Calendar API 설계 (리소스 중심)

| 기능명 | API명 | 엔드포인트 | Method | 설명 |
| :--- | :--- | :--- | :--- | :--- |
| **일정 통합 조회** | `events` | `/api/calendar/events` | `GET` | 전체 일정 조회 (필터/검색 포함) |
| **날짜별 일정 조회** | `events` | `/api/calendar/events?date=YYYY-MM-DD` | `GET` | 특정 날짜 일정 조회 |
| **일정 검색** | `events` | `/api/calendar/events?keyword=검색어` | `GET` | 일정 제목/내용 검색 |
| **유형별 조회** | `events` | `/api/calendar/events?type=academic` | `GET` | 학사(`academic`)/개인(`personal`) 필터 |
| **일정 상세 조회** | `event` | `/api/calendar/events/{eventId}` | `GET` | 특정 일정 1개 상세 조회 |
| **개인 일정 등록** | `eventCreate` | `/api/calendar/events` | `POST` | 새로운 일정 생성 (JSON Body 사용) |
| **일정 수정** | `eventUpdate` | `/api/calendar/events/{eventId}` | `PUT` | 기존 일정 수정 |
| **일정 삭제** | `eventDelete` | `/api/calendar/events/{eventId}` | `DELETE` | 일정 삭제 (학사 일정은 불가) |
| **완료 상태 토글** | `eventToggle` | `/api/calendar/events/{eventId}/toggle` | `PATCH` | 완료/미완료 상태 반전 (True/False) |

---

### 💡 필터 조합 예시 (Query Parameters)
- **학생 1번의 전체 일정 (학사 포함)**: `GET /api/calendar/events?studentId=1`
- **학생 1번의 오늘 할 일만 조회**: `GET /api/calendar/events?studentId=1&date=2026-05-22`
- **학교 공식 일정 중 '축제' 검색**: `GET /api/calendar/events?type=academic&keyword=축제`

---

## 🔒 데이터 정책 및 보안

- **학사 일정 (`studentId` is NULL)**: 모든 사용자가 읽을 수만 있으며, API를 통한 생성/수정/삭제/상태변경이 **절대 불가**합니다.
- **개인 일정 (`studentId` is NOT NULL)**: 해당 학생 본인만 모든 권한(CRUD)을 가집니다.
- **조회 제한**: `studentId` 파라미터를 사용하면 해당 학생의 일정과 학사 일정만 노출되며, **타인의 개인 일정은 노출되지 않습니다.**

---

## 🎨 데이터 상세 (Enum & Fields)

### 1. 필드 설명
| 필드명 | 타입 | 설명 |
| :--- | :--- | :--- |
| **scheduleId** | Long | 일정 고유 ID (자동 생성) |
| **title** | String | 일정 제목 (필수) |
| **content** | String | 일정 상세 내용 |
| **date** | LocalDate | 일정 날짜 (YYYY-MM-DD, 필수) |
| **studentId** | Long | 학생 ID (학사 일정일 경우 `null`) |
| **isCompleted** | Boolean | 완료 여부 |
| **category** | String | 일정 분류 (`STUDY`, `WORK`, `PRIVATE`, `ACADEMIC`, `OTHER`) |
| **priority** | String | 중요도 (`HIGH`, `MEDIUM`, `LOW`) |

---

## 🚀 Postman 테스트 데이터

### 1. 개인 일정 생성 (POST)
```bash
POST http://localhost:8080/api/calendar/events
Content-Type: application/json

{
  "title": "기말고사 공부",
  "content": "도서관에서 알고리즘 공부",
  "date": "2026-06-15",
  "studentId": 1,
  "category": "STUDY",
  "priority": "HIGH"
}
```
**응답 예시 (201 Created)**
```json
{
  "scheduleId": 1,
  "title": "기말고사 공부",
  "content": "도서관에서 알고리즘 공부",
  "date": "2026-06-15",
  "studentId": 1,
  "isCompleted": false,
  "category": "STUDY",
  "priority": "HIGH"
}
```

### 2. 일정 목록 조회 (GET)
```bash
GET http://localhost:8080/api/calendar/events?studentId=1
```
**응답 예시 (200 OK)**
```json
[
  {
    "scheduleId": 100,
    "title": "여름방학 시작",
    "content": "공식 학사 일정",
    "date": "2026-06-22",
    "studentId": null,
    "isCompleted": false,
    "category": "ACADEMIC",
    "priority": "MEDIUM"
  },
  {
    "scheduleId": 1,
    "title": "기말고사 공부",
    "content": "도서관에서 알고리즘 공부",
    "date": "2026-06-15",
    "studentId": 1,
    "isCompleted": false,
    "category": "STUDY",
    "priority": "HIGH"
  }
]
```

### 3. 완료 상태 토글 (PATCH)
```bash
PATCH http://localhost:8080/api/calendar/events/1/toggle
```
**응답 예시 (200 OK)**
```json
{
  "scheduleId": 1,
  "isCompleted": true,
  ...
}
```

---

## 테스트 시나리오 (순서대로 실행)
# 1. 개인 일정 생성
POST http://localhost:8080/api/calendar/events
{
  "title": "기말고사 공부",
  "date": "2026-06-15",
  "studentId": 1
}
# 응답에서 scheduleId=1 확인

# 2. 내 일정 목록 확인
GET http://localhost:8080/api/calendar/events?studentId=1

# 3. 일정 검색 (키워드)
GET http://localhost:8080/api/calendar/events?keyword=기말

# 4. 일정 상태 완료 처리
PATCH http://localhost:8080/api/calendar/events/1/toggle

# 5. 일정 수정
PUT http://localhost:8080/api/calendar/events/1
{
  "title": "수정된 제목",
  "date": "2026-06-15",
  "studentId": 1
}

# 6. 학사 일정 수정 시도 (에러 확인)
PUT http://localhost:8080/api/calendar/events/100
{ "title": "수정시도", "studentId": 1 }
# 403 Forbidden 응답 확인

# 7. 일정 삭제
DELETE http://localhost:8080/api/calendar/events/1

## 에러 응답
- **404 Not Found**: 존재하지 않는 일정 ID
- **403 Forbidden**: 학사 일정을 수정/삭제/상태변경 하려고 할 때
- **400 Bad Request**: 필수 필드 누락 또는 잘못된 요청 형식

## 주의사항
- **학사 일정 보호**: `studentId`가 `null`인 일정은 시스템 전용이며, API를 통한 수정/삭제/완료처리가 금지됩니다.
- **조회 정책**: `studentId` 쿼리 파라미터를 누락하면 전체 일정이 조회되나, 실무에서는 본인의 ID를 포함하여 호출하는 것을 권장합니다.
- **데이터 초기화**: 서버 시작 시 `CalendarDataInitializer`를 통해 기본 학사 일정 데이터가 생성됩니다.

---

## 💡 Frontend (React) 통합 가이드

1. **로그인 연동**: 사용자가 로그인하면 학생 ID를 기억했다가, 모든 `GET` 요청 시 `?studentId={ID}`를 반드시 포함하십시오.
2. **UI 구분**: `studentId`가 `null`인 데이터는 수정/삭제 버튼을 숨기고, 달력에서 강조색을 다르게 표시하십시오.
3. **완료 처리**: 체크박스 클릭 시 `PATCH /api/calendar/events/{eventId}/toggle`을 호출하면 간단하게 상태를 동기화할 수 있습니다.
