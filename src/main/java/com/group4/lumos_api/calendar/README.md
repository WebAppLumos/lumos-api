# 📅 Lumos API - Calendar & To-Do Module

이 모듈은 학사 일정(공통) 및 학생 개인의 To-Do 일정을 통합 관리하는 기능을 제공합니다.

---

## 🚀 시작하기 (How to Run)

터미널에서 프로젝트 루트 폴더로 이동한 뒤 아래 명령어를 입력하여 서버를 실행합니다:

```bash
# Windows (PowerShell)
.\gradlew bootRun

# macOS / Linux
./gradlew bootRun
```

서버가 실행되면 `http://localhost:8080`에서 API를 호출할 수 있습니다.

---

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

| 기능명 | API명 | 엔드포인트 | Method | 설명 |
| :--- | :--- | :--- | :--- | :--- |
| **일정 통합 조회** | `events` | `/api/calendar/events` | `GET` | 전체 일정 조회 (필터/검색 포함) |
| **날짜별 일정 조회** | `events` | `/api/calendar/events?date=YYYY-MM-DD` | `GET` | 특정 날짜 일정 조회 |
| **일정 검색** | `events` | `/api/calendar/events?keyword=검색어` | `GET` | 일정 제목/내용 검색 |
| **유형별 조회** | `events` | `/api/calendar/events?type=academic` | `GET` | 학사(`academic`)/개인(`personal`) 필터 |
| **일정 상세 조회** | `event` | `/api/calendar/events/{scheduleId}` | `GET` | 특정 일정 1개 상세 조회 |
| **개인 일정 등록** | `eventCreate` | `/api/calendar/events` | `POST` | 새로운 일정 생성 (JSON Body 사용) |
| **일정 수정** | `eventUpdate` | `/api/calendar/events/{scheduleId}` | `PUT` | 기존 일정 수정 |
| **일정 삭제** | `eventDelete` | `/api/calendar/events/{scheduleId}` | `DELETE` | 일정 삭제 (학사 일정은 불가) |
| **완료 상태 토글** | `eventToggle` | `/api/calendar/events/{scheduleId}/toggle` | `PATCH` | 완료/미완료 상태 반전 (True/False) |

---

## 🔒 데이터 정책 및 보안

- **학사 일정 (`studentId` is NULL)**: 모든 사용자가 읽을 수만 있으며, API를 통한 생성/수정/삭제/상태변경이 **절대 불가**합니다.
- **개인 일정 (`studentId` is NOT NULL)**: 해당 학생 본인만 모든 권한(CRUD)을 가집니다.
- **조회 제한**: `studentId` 파라미터를 사용하면 해당 학생의 일정과 학사 일정만 노출되며, **타인의 개인 일정은 노출되지 않습니다.**

---

## 🎨 데이터 상세 (Enum & Fields)

### 1. 필드 설명
| 필드명 | 타입 | 필수여부 | 설명 |
| :--- | :--- | :--- | :--- |
| **scheduleId** | Long | 자동생성 | 일정 고유 ID |
| **title** | String | **필수** | 일정 제목 |
| **content** | String | 선택 | 일정 상세 내용 |
| **date** | LocalDate | **필수** | 일정 날짜 (YYYY-MM-DD) |
| **studentId** | Long | **필수** | 학생 ID (개인 일정일 경우 필수) |
| **isCompleted** | boolean | 선택 | 완료 여부 (기본값: `false`) |
| **category** | String | 선택 | 일정 분류 (대문자 필수: `STUDY`, `WORK`, `PRIVATE`, `ACADEMIC`, `OTHER`) |
| **priority** | String | 선택 | 중요도 (대문자 필수: `HIGH`, `MEDIUM`, `LOW`) |

---

## 🧪 테스트 시나리오 (Full Test Suite)

아래 순서대로 API를 호출하며 모든 기능을 검증하십시오. (모든 주소는 `http://localhost:8080` 기준)
**※ 주의: `{scheduleId}` 부분은 1단계 응답에서 받은 실제 ID 숫자로 바꿔서 테스트하세요.**

### 1단계: 개인 일정 생성 (POST)
- **Endpoint**: `POST /api/calendar/events`
- **Body**:
  ```json
  {
    "title": "알고리즘 기말고사 공부",
    "content": "도서관 4층에서 빡공하기",
    "date": "2026-06-15",
    "studentId": 1,
    "category": "STUDY",
    "priority": "HIGH"
  }
  ```
- **확인**: 생성된 `scheduleId`를 확인하세요. (이후 단계에서 사용)

### 2단계: 통합 일정 조회 (GET)
- **Endpoint**: `GET /api/calendar/events?studentId=1`
- **설명**: 학사 일정(null)과 내 일정(1번)이 모두 잘 섞여서 나오는지 확인합니다.

### 3단계: 특정 날짜로 필터링 (GET)
- **Endpoint**: `GET /api/calendar/events?studentId=1&date=2026-06-15`
- **설명**: 방금 생성한 날짜의 일정만 필터링되어 나오는지 확인합니다.

### 4단계: 키워드 검색 (GET)
- **Endpoint**: `GET /api/calendar/events?studentId=1&keyword=알고리즘`
- **설명**: 제목이나 내용에 '알고리즘'이 포함된 일정만 검색되는지 확인합니다.

### 5단계: 유형별 필터링 (GET)
- **학사 일정만 보기**: `GET /api/calendar/events?type=academic`
- **개인 일정만 보기**: `GET /api/calendar/events?studentId=1&type=personal`

### 6단계: 일정 상세 조회 (GET)
- **Endpoint**: `GET /api/calendar/events/{scheduleId}`
- **설명**: 특정 일정 1개의 상세 정보만 가져오는지 확인합니다.

### 7단계: 완료 상태 토글 (PATCH)
- **Endpoint**: `PATCH /api/calendar/events/{scheduleId}/toggle`
- **설명**: 체크박스 클릭 시 `isCompleted`가 `true`로 바뀌는지 확인합니다.

### 8단계: 일정 내용 수정 (PUT)
- **Endpoint**: `PUT /api/calendar/events/{scheduleId}`
- **Body**:
  ```json
  {
    "title": "수정된 알고리즘 공부",
    "content": "카페로 장소 변경",
    "date": "2026-06-15",
    "studentId": 1,
    "isCompleted": true,
    "category": "STUDY",
    "priority": "MEDIUM"
  }
  ```

### 9단계: 일정 삭제 (DELETE)
- **Endpoint**: `DELETE /api/calendar/events/{scheduleId}`
- **확인**: 삭제 후 다시 조회했을 때 데이터가 나오지 않아야 합니다.

---

## ⚠️ 주의사항 (자주 발생하는 오류)

1. **Enum 대소문자**: `category`, `priority`는 반드시 **대문자**여야 합니다. (`study` ❌ -> `STUDY` ✅)
2. **학생 ID**: 현재 `Long` 타입이므로 숫자로 입력하십시오.
3. **날짜**: 반드시 `YYYY-MM-DD` 형식을 사용하십시오.
4. **학사 일정 보호**: `studentId`가 `null`인 일정은 `PUT`, `DELETE`, `PATCH` 요청 시 `403 Forbidden` 에러가 발생합니다.

---

## 💡 Frontend (React) 통합 가이드

1. **로그인 연동**: 사용자가 로그인하면 학생 ID를 기억했다가, 모든 `GET` 요청 시 `?studentId={ID}`를 반드시 포함하십시오.
2. **UI 구분**: `studentId`가 `null`인 데이터는 수정/삭제 버튼을 숨기고, 달력에서 강조색을 다르게 표시하십시오.
3. **완료 처리**: 체크박스 클릭 시 `PATCH /api/calendar/events/{scheduleId}/toggle`을 호출하면 간단하게 상태를 동기화할 수 있습니다.
