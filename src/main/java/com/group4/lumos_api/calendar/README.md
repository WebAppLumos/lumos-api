# 📅 Lumos API - Calendar Module

이 모듈은 학사 일정(공통) 및 학생 개인 일정을 관리하는 기능을 제공합니다. 사용자는 자신의 일정을 자유롭게 관리할 수 있으며, 시스템에서 제공하는 학사 일정을 함께 조회할 수 있습니다.

## 📂 패키지 구조

```text
com.group4.lumos_api.calendar/
├── controller/
│   └── CalendarEventController    # API 엔드포인트 및 요청 처리
├── service/
│   └── CalendarEventService       # 비즈니스 로직 및 권한 검증
├── repository/
│   └── CalendarEventRepository    # 데이터베이스 접근 (JPQL 포함)
├── entity/
│   └── CalendarEvent              # DB 테이블 매핑 엔티티
├── dto/
│   ├── CalendarEventRequest       # 생성/수정용 데이터 객체
│   └── CalendarEventResponse      # 응답용 데이터 객체
├── README.md                      # (현재 파일) API 문서
└── CalendarDataInitializer.java   # 시스템 가동 시 초기 데이터 생성
```

---

## 🔒 데이터 정책 및 보안 (핵심 로직)

이 시스템은 `studentId` 필드를 기준으로 **학사 일정**과 **개인 일정**을 철저히 구분합니다.

| 구분 | `studentId` 값 | 권한 |
| :--- | :--- | :--- |
| **학사 일정** | `null` | 모든 사용자 조회 가능, **API를 통한 수정/삭제/추가 불가** |
| **개인 일정** | 특정 숫자 (ID) | 해당 학생 본인만 CRUD 가능 |

### 방어 로직 상세
- **생성(Create)**: `studentId`가 `null`인 요청은 `400 Bad Request`로 차단됩니다.
- **수정(Update)**: 대상 데이터의 `studentId`가 `null`이거나 요청의 `studentId`를 `null`로 바꾸려는 시도는 차단됩니다.
- **삭제(Delete)**: 대상 데이터의 `studentId`가 `null`이면 `403 Forbidden`을 반환합니다.

---

## 🚀 API 명세 (Endpoints)

### 1. 통합 일정 조회 (GET)
학생 본인의 일정과 공통 학사 일정을 합쳐서 조회합니다.

- **URL**: `GET /api/calendar-events`
- **Query Params**:
  - `studentId` (Long): 현재 로그인한 학생의 ID.
  - `date` (LocalDate, Optional): `YYYY-MM-DD` 형식으로 특정 날짜만 필터링.
- **특징**: `studentId=1`로 요청 시 `studentId IS NULL OR studentId = 1` 조건으로 검색됩니다.

### 2. 일정 상세 조회 (GET)
특정 일정의 상세 내용을 가져옵니다.

- **URL**: `GET /api/calendar-events/{id}`

### 3. 개인 일정 등록 (POST)
새로운 개인 일정을 추가합니다.

- **URL**: `POST /api/calendar-events`
- **Body**:
  ```json
  {
    "title": "DB 과제 마감",
    "content": "LMS에 23:59까지 제출",
    "date": "2026-03-20",
    "studentId": 1
  }
  ```

### 4. 개인 일정 수정 (PUT)
기존에 등록한 개인 일정을 수정합니다.

- **URL**: `PUT /api/calendar-events/{id}`
- **Body**: 상동

### 5. 개인 일정 삭제 (DELETE)
특정 개인 일정을 삭제합니다.

- **URL**: `DELETE /api/calendar-events/{id}`

---

## 🛠 초기 데이터 (Data Seeding)

시스템 가동 시 `CalendarDataInitializer`에 의해 다음과 같은 학사 일정이 자동으로 생성됩니다 (DB가 비어있을 경우).
- 3월: 개강 일정
- 4월: 중간고사 기간
- 5월: 봄 축제
- 6월: 종강 일정

---

## 💡 Frontend (React) 통합 가이드

1. **데이터 렌더링**: 응답 데이터 중 `studentId`가 `null`인 객체는 달력에서 다른 색상(예: 노란색)으로 표시하여 학사 일정임을 구분하십시오.
2. **UI 제어**: `studentId`가 `null`인 데이터에 대해서는 수정/삭제 버튼을 비활성화하거나 숨김 처리하십시오.
3. **API 호출**: 사용자가 로그인하면 해당 사용자의 고유 ID를 전역 상태(Redux, Context 등)에 저장하고, 모든 `GET` 요청 시 `?studentId={id}`를 붙여서 호출하십시오.
