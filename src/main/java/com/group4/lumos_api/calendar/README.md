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

## 🚀 API 명세 (Endpoints)

### 📌 Calendar API 설계 (리소스 중심)

| 기능명 | API명 | 엔드포인트 | Method | 설명 |
| :--- | :--- | :--- | :--- | :--- |
| **일정 통합 조회** | `events` | `/api/calendar/events` | `GET` | 전체 일정 조회 (필터/검색 포함) |
| **날짜별 일정 조회** | `events` | `/api/calendar/events?date=YYYY-MM-DD` | `GET` | 특정 날짜 일정 조회 |
| **일정 검색** | `events` | `/api/calendar/events?keyword=검색어` | `GET` | 일정 제목/내용 검색 |
| **유형별 조회** | `events` | `/api/calendar/events?type=academic` | `GET` | 학사/개인 등 타입 필터 조회 |
| **일정 상세 조회** | `event` | `/api/calendar/events/{eventId}` | `GET` | 특정 일정 1개 조회 |
| **개인 일정 등록** | `eventCreate` | `/api/calendar/events` | `POST` | 새로운 일정 생성 |
| **일정 수정** | `eventUpdate` | `/api/calendar/events/{eventId}` | `PUT` | 기존 일정 수정 |
| **일정 삭제** | `eventDelete` | `/api/calendar/events/{eventId}` | `DELETE` | 일정 삭제 |
| **완료 상태 토글** | `eventToggle` | `/api/calendar/events/{eventId}/toggle` | `PATCH` | 완료/미완료 상태 변경 |

---

### 💡 필터 조합 예시
- **특정 학생의 학사+개인 일정 조회**: `GET /api/calendar/events?studentId=1`
- **특정 날짜의 학사 일정만 조회**: `GET /api/calendar/events?date=2026-05-22&type=academic`
- **전체 일정 중 '시험' 키워드 검색**: `GET /api/calendar/events?keyword=시험`

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
- **상태 변경(Toggle)**: 학사 일정의 완료 여부는 변경할 수 없습니다.

---

## 🎨 데이터 타입 상세 (Enum)

| 필드명 | 값 (Enum) | 설명 |
| :--- | :--- | :--- |
| **Category** | `STUDY`, `WORK`, `PRIVATE`, `ACADEMIC`, `OTHER` | 일정 분류 (예: 학업, 알바, 개인 등) |
| **Priority** | `HIGH`, `MEDIUM`, `LOW` | 중요도 (빨강/노랑/초록 등 UI 활용) |

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
