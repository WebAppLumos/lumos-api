# Semester API

학기(학기) 정보를 관리하는 API입니다.

## 개요

학기는 수업의 최상위 개념으로, 수업들이 속하는 학기를 생성하고 관리합니다.

## 데이터 모델

```json
{
  "id": 1,
  "name": "2024-1학기",
  "startDate": "2024-03-04",
  "endDate": "2024-06-14",
  "isActive": true,
  "createdAt": "2026-05-18T10:30:00",
  "updatedAt": "2026-05-18T10:30:00"
}
```

### 필드 설명
- `id`: 학기 고유 ID (자동 생성)
- `name`: 학기명 (예: "2024-1학기", "2024 Spring")
- `startDate`: 시작 날짜 (ISO 8601 형식: YYYY-MM-DD)
- `endDate`: 종료 날짜 (ISO 8601 형식: YYYY-MM-DD)
- `isActive`: 활성 여부 (기본값: true)
- `createdAt`: 생성 일시 (자동 기록)
- `updatedAt`: 수정 일시 (자동 기록)

## API 엔드포인트

| 기능 | 메서드 | 엔드포인트 | 설명 |
|------|--------|-----------|------|
| 생성 | POST | `/api/semesters` | 새 학기 생성 |
| 목록 조회 | GET | `/api/semesters` | 모든 학기 조회 |
| 상세 조회 | GET | `/api/semesters/{id}` | 특정 학기 조회 |
| 수정 | PATCH | `/api/semesters/{id}` | 학기 정보 수정 |
| 삭제 | DELETE | `/api/semesters/{id}` | 학기 삭제 |

## Postman 테스트 데이터

### 1. 학기 생성 (POST)
```bash
POST http://localhost:8080/api/semesters
Content-Type: application/json

{
  "name": "2024-1학기",
  "startDate": "2024-03-04",
  "endDate": "2024-06-14",
  "isActive": true
}
```

**응답 예시 (201 Created)**
```json
{
  "id": 1,
  "name": "2024-1학기",
  "startDate": "2024-03-04",
  "endDate": "2024-06-14",
  "isActive": true,
  "createdAt": "2026-05-18T10:30:00",
  "updatedAt": "2026-05-18T10:30:00"
}
```

### 2. 학기 목록 조회 (GET)
```bash
GET http://localhost:8080/api/semesters
```

**응답 예시 (200 OK)**
```json
[
  {
    "id": 1,
    "name": "2024-1학기",
    "startDate": "2024-03-04",
    "endDate": "2024-06-14",
    "isActive": true,
    "createdAt": "2026-05-18T10:30:00",
    "updatedAt": "2026-05-18T10:30:00"
  },
  {
    "id": 2,
    "name": "2024-2학기",
    "startDate": "2024-09-02",
    "endDate": "2024-12-13",
    "isActive": true,
    "createdAt": "2026-05-18T11:00:00",
    "updatedAt": "2026-05-18T11:00:00"
  }
]
```

### 3. 학기 상세 조회 (GET)
```bash
GET http://localhost:8080/api/semesters/1
```

**응답 예시 (200 OK)**
```json
{
  "id": 1,
  "name": "2024-1학기",
  "startDate": "2024-03-04",
  "endDate": "2024-06-14",
  "isActive": true,
  "createdAt": "2026-05-18T10:30:00",
  "updatedAt": "2026-05-18T10:30:00"
}
```

### 4. 학기 정보 수정 (PATCH)
```bash
PATCH http://localhost:8080/api/semesters/1
Content-Type: application/json

{
  "name": "2024-1학기 (수정)",
  "endDate": "2024-06-21",
  "isActive": true
}
```

**응답 예시 (200 OK)**
```json
{
  "id": 1,
  "name": "2024-1학기 (수정)",
  "startDate": "2024-03-04",
  "endDate": "2024-06-21",
  "isActive": true,
  "createdAt": "2026-05-18T10:30:00",
  "updatedAt": "2026-05-18T11:30:00"
}
```

### 5. 학기 삭제 (DELETE)
```bash
DELETE http://localhost:8080/api/semesters/1
```

**응답 예시 (204 No Content)**
```
(응답 본문 없음)
```

## 에러 응답

- **404 Not Found**: 존재하지 않는 학기 ID
- **400 Bad Request**: 잘못된 요청 형식

## 주의사항

- 학기 삭제 시 해당 학기에 속한 수업이 함께 삭제됩니다.
- `startDate`는 `endDate`보다 빨라야 합니다.
