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

### 1. Request Body (POST/PUT)
```json
{
  "title": "필수 제목",
  "content": "선택적 내용",
  "date": "2026-05-22",
  "studentId": 1,
  "category": "STUDY",  // 아래 옵션 참고
  "priority": "HIGH",   // 아래 옵션 참고
  "isCompleted": false
}
```

### 2. Enum 옵션 리스트
| 필드명 | 허용되는 값 (Enum) | 설명 |
| :--- | :--- | :--- |
| **Category** | `STUDY`, `WORK`, `PRIVATE`, `ACADEMIC`, `OTHER` | 일정 분류 |
| **Priority** | `HIGH`, `MEDIUM`, `LOW` | 중요도 (UI 색상 대응용) |

---

## 💡 Frontend (React) 통합 가이드

1. **로그인 연동**: 사용자가 로그인하면 학생 ID를 기억했다가, 모든 `GET` 요청 시 `?studentId={ID}`를 반드시 포함하십시오.
2. **UI 구분**: `studentId`가 `null`인 데이터는 수정/삭제 버튼을 숨기고, 달력에서 강조색을 다르게 표시하십시오.
3. **완료 처리**: 체크박스 클릭 시 `PATCH /api/calendar/events/{eventId}/toggle`을 호출하면 간단하게 상태를 동기화할 수 있습니다.
