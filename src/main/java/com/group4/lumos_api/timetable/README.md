# Timetable API

시간표(Timetable) 정보를 관리하는 API입니다.

## 개요

시간표는 특정 학기에 속하는 시간표 묶음을 나타냅니다. 시간표 생성 시에는 반드시 학기를 지정해야 하며, 수업 배치는 Entry API를 통해 관리합니다.

URL 규칙에 따라 생성/목록 조회는 상위 리소스인 학기 아래에 중첩되고, 단건 상세/수정/삭제는 `/api/timetables/{timetableId}`로 평탄화됩니다.

## 데이터 모델

```json
{
  "id": 1,
  "semesterId": 1,
  "title": "2026-1학기 기본 시간표",
  "createdAt": "2026-05-22T10:30:00",
  "updatedAt": "2026-05-22T10:30:00"
}
```

### 필드 설명
- `id`: 시간표 고유 ID (자동 생성)
- `semesterId`: 소속 학기 ID (필수)
- `title`: 시간표 이름 (예: "2026-1학기 기본 시간표")
- `createdAt`: 생성 일시 (자동 기록)
- `updatedAt`: 수정 일시 (자동 기록)

## API 엔드포인트

| 기능 | 메서드 | 엔드포인트 | 설명 |
|------|--------|-----------|------|
| 생성 | POST | `/api/semesters/{semesterId}/timetables` | 학기에 속한 시간표 생성 |
| 목록 조회 | GET | `/api/semesters/{semesterId}/timetables` | 학기별 시간표 목록 조회 |
| 상세 조회 | GET | `/api/timetables/{timetableId}` | 특정 시간표 상세 조회 |
| 수정 | PATCH | `/api/timetables/{timetableId}` | 시간표 정보 수정 |
| 삭제 | DELETE | `/api/timetables/{timetableId}` | 시간표 삭제 |

## Postman 테스트 데이터

### 사전 준비
- 먼저 Semester API로 학기를 생성하고 ID를 확인하세요.
- 아래 예시에서는 `semesterId=1`, `timetableId=1`을 사용합니다.

### 1. 시간표 생성 (POST)
```bash
POST http://localhost:8080/api/semesters/1/timetables
Content-Type: application/json

{
  "title": "2026-1학기 기본 시간표"
}
```

**응답 예시 (201 Created)**
```json
{
  "id": 1,
  "semesterId": 1,
  "title": "2026-1학기 기본 시간표",
  "createdAt": "2026-05-22T10:30:00",
  "updatedAt": "2026-05-22T10:30:00"
}
```

### 2. 시간표 목록 조회 (GET)
```bash
GET http://localhost:8080/api/semesters/1/timetables
```

**응답 예시 (200 OK)**
```json
[
  {
    "id": 1,
    "semesterId": 1,
    "title": "2026-1학기 기본 시간표",
    "createdAt": "2026-05-22T10:30:00",
    "updatedAt": "2026-05-22T10:30:00"
  },
  {
    "id": 2,
    "semesterId": 1,
    "title": "시험 기간 시간표",
    "createdAt": "2026-05-22T10:40:00",
    "updatedAt": "2026-05-22T10:40:00"
  }
]
```

### 3. 시간표 상세 조회 (GET)
```bash
GET http://localhost:8080/api/timetables/1
```

**응답 예시 (200 OK)**
```json
{
  "id": 1,
  "semesterId": 1,
  "title": "2026-1학기 기본 시간표",
  "createdAt": "2026-05-22T10:30:00",
  "updatedAt": "2026-05-22T10:30:00"
}
```

### 4. 시간표 정보 수정 (PATCH)
```bash
PATCH http://localhost:8080/api/timetables/1
Content-Type: application/json

{
  "title": "2026-1학기 최종 시간표"
}
```

**응답 예시 (200 OK)**
```json
{
  "id": 1,
  "semesterId": 1,
  "title": "2026-1학기 최종 시간표",
  "createdAt": "2026-05-22T10:30:00",
  "updatedAt": "2026-05-22T11:00:00"
}
```

### 5. 시간표 삭제 (DELETE)
```bash
DELETE http://localhost:8080/api/timetables/1
```

**응답 예시 (204 No Content)**
```
(응답 본문 없음)
```

## 테스트 시나리오 (순서대로 실행)

```bash
# 1. 학기 생성 (Semester API)
POST http://localhost:8080/api/semesters
{
  "title": "2026-1학기",
  "startDate": "2026-03-02",
  "endDate": "2026-06-19",
  "isActive": true
}
# 응답에서 id=1 확인

# 2. 시간표 생성
POST http://localhost:8080/api/semesters/1/timetables
{
  "title": "2026-1학기 기본 시간표"
}
# 응답에서 id=1 확인

# 3. 시간표 목록 조회
GET http://localhost:8080/api/semesters/1/timetables

# 4. 시간표 상세 조회
GET http://localhost:8080/api/timetables/1

# 5. 시간표 수정
PATCH http://localhost:8080/api/timetables/1
{
  "title": "2026-1학기 최종 시간표"
}

# 6. 시간표 삭제
DELETE http://localhost:8080/api/timetables/1
```

## 에러 응답

- **404 Not Found**: 존재하지 않는 학기 또는 시간표 ID
- **400 Bad Request**: 잘못된 요청 형식 또는 유효하지 않은 필드 값
- **500 Internal Server Error**: 서버 오류

## 주의사항

- 시간표 생성/목록 조회는 `semesterId`를 경로에 포함하며, 해당 학기가 존재해야 합니다.
- 단건 상세/수정/삭제는 `timetableId`만으로 식별합니다.
- PATCH 요청 시 null 값인 필드는 수정되지 않습니다.
- 경로에서 `{semesterId}`와 `{timetableId}`는 각각 학기 ID와 시간표 ID로 치환해야 합니다.

## EDWARD 동기화

EDWARD 시간표 import 시 `"EDWARD 동기화"` 제목의 시간표가 생성·갱신됩니다. [sync/README.md](../sync/README.md) 참고.
