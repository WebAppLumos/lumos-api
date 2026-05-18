# Course API

수업(과목) 정보를 관리하는 API입니다.

## 개요

수업은 특정 학기에 속하는 과목 정보를 나타냅니다. 수업 생성 시에는 반드시 학기를 지정해야 합니다.

## 데이터 모델

```json
{
  "id": 1,
  "semesterId": 1,
  "name": "자료구조",
  "classroom": "공학관 302",
  "professorName": "김철수",
  "color": "#4F46E5",
  "createdAt": "2026-05-18T10:30:00",
  "updatedAt": "2026-05-18T10:30:00"
}
```

### 필드 설명
- `id`: 수업 고유 ID (자동 생성)
- `semesterId`: 소속 학기 ID (필수)
- `name`: 수업명 (예: "자료구조", "웹 프로그래밍")
- `classroom`: 강의실 (예: "공학관 302")
- `professorName`: 교수명 (예: "김철수")
- `color`: 수업 색상 (16진수 색상 코드, 예: "#4F46E5")
- `createdAt`: 생성 일시 (자동 기록)
- `updatedAt`: 수정 일시 (자동 기록)

## API 엔드포인트

| 기능 | 메서드 | 엔드포인트 | 설명 |
|------|--------|-----------|------|
| 생성 | POST | `/api/semesters/{semesterId}/courses` | 학기에 속한 수업 생성 |
| 목록 조회 | GET | `/api/semesters/{semesterId}/courses` | 학기별 수업 목록 조회 |
| 상세 조회 | GET | `/api/semesters/{semesterId}/courses/{courseId}` | 특정 수업 정보 조회 |
| 수정 | PATCH | `/api/semesters/{semesterId}/courses/{courseId}` | 수업 정보 수정 |
| 삭제 | DELETE | `/api/semesters/{semesterId}/courses/{courseId}` | 수업 삭제 |

## Postman 테스트 데이터

### 사전 준비
- 먼저 Semester API로 학기를 생성하고 ID를 확인하세요.
- 아래 예시에서는 `semesterId=1`, `courseId=1`을 사용합니다.

### 1. 수업 생성 (POST)
```bash
POST http://localhost:8080/api/semesters/1/courses
Content-Type: application/json

{
  "name": "자료구조",
  "classroom": "공학관 302",
  "professorName": "김철수",
  "color": "#4F46E5"
}
```

**응답 예시 (201 Created)**
```json
{
  "id": 1,
  "semesterId": 1,
  "name": "자료구조",
  "classroom": "공학관 302",
  "professorName": "김철수",
  "color": "#4F46E5",
  "createdAt": "2026-05-18T10:35:00",
  "updatedAt": "2026-05-18T10:35:00"
}
```

### 2. 수업 목록 조회 (GET)
```bash
GET http://localhost:8080/api/semesters/1/courses
```

**응답 예시 (200 OK)**
```json
[
  {
    "id": 1,
    "semesterId": 1,
    "name": "자료구조",
    "classroom": "공학관 302",
    "professorName": "김철수",
    "color": "#4F46E5",
    "createdAt": "2026-05-18T10:35:00",
    "updatedAt": "2026-05-18T10:35:00"
  },
  {
    "id": 2,
    "semesterId": 1,
    "name": "데이터베이스",
    "classroom": "공학관 305",
    "professorName": "이영희",
    "color": "#22C55E",
    "createdAt": "2026-05-18T10:40:00",
    "updatedAt": "2026-05-18T10:40:00"
  }
]
```

### 3. 수업 상세 조회 (GET)
```bash
GET http://localhost:8080/api/semesters/1/courses/1
```

**응답 예시 (200 OK)**
```json
{
  "id": 1,
  "semesterId": 1,
  "name": "자료구조",
  "classroom": "공학관 302",
  "professorName": "김철수",
  "color": "#4F46E5",
  "createdAt": "2026-05-18T10:35:00",
  "updatedAt": "2026-05-18T10:35:00"
}
```

### 4. 수업 정보 수정 (PATCH)
```bash
PATCH http://localhost:8080/api/semesters/1/courses/1
Content-Type: application/json

{
  "name": "자료구조 심화",
  "classroom": "공학관 305",
  "professorName": "김철수",
  "color": "#22C55E"
}
```

**응답 예시 (200 OK)**
```json
{
  "id": 1,
  "semesterId": 1,
  "name": "자료구조 심화",
  "classroom": "공학관 305",
  "professorName": "김철수",
  "color": "#22C55E",
  "createdAt": "2026-05-18T10:35:00",
  "updatedAt": "2026-05-18T11:45:00"
}
```

### 5. 수업 삭제 (DELETE)
```bash
DELETE http://localhost:8080/api/semesters/1/courses/1
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
  "name": "2024-1학기",
  "startDate": "2024-03-04",
  "endDate": "2024-06-14",
  "isActive": true
}
# 응답에서 id=1 확인

# 2. 수업 생성
POST http://localhost:8080/api/semesters/1/courses
{
  "name": "자료구조",
  "classroom": "공학관 302",
  "professorName": "김철수",
  "color": "#4F46E5"
}
# 응답에서 id=1 확인

# 3. 수업 목록 조회
GET http://localhost:8080/api/semesters/1/courses

# 4. 수업 상세 조회
GET http://localhost:8080/api/semesters/1/courses/1

# 5. 수업 수정
PATCH http://localhost:8080/api/semesters/1/courses/1
{
  "classroom": "공학관 305",
  "color": "#22C55E"
}

# 6. 수업 삭제
DELETE http://localhost:8080/api/semesters/1/courses/1

# 7. 학기 삭제 (Semester API)
DELETE http://localhost:8080/api/semesters/1
```

## 에러 응답

- **404 Not Found**: 존재하지 않는 학기 또는 수업 ID
- **400 Bad Request**: 잘못된 요청 형식 또는 유효하지 않은 필드 값
- **500 Internal Server Error**: 서버 오류

## 주의사항

- 수업 생성 시 `semesterId`에 해당하는 학기가 존재해야 합니다.
- PATCH 요청 시 null 값인 필드는 수정되지 않습니다.
- 색상 필드는 16진수 색상 코드 형식이어야 합니다 (예: #RRGGBB).
- 경로에서 `{semesterId}`와 `{courseId}`는 각각 학기 ID와 수업 ID로 치환해야 합니다.
