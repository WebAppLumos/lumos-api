# Course API

수업(Course) 정보를 관리하는 API입니다.

## 개요

수업은 특정 학기에 속하는 과목 정보를 나타냅니다. 수업 생성 시에는 반드시 학기를 지정해야 합니다. 수업 난이도(`difficultyLevel`)는 수업의 속성으로, 생성/수정 요청 본문으로 설정하고 조회 응답에 포함됩니다. 별도의 난이도 엔드포인트는 없습니다.

URL 규칙에 따라 생성/목록 조회는 상위 리소스인 학기 아래에 중첩되고, 단건 상세/수정/삭제는 `/api/courses/{courseId}`로 평탄화됩니다.

## 데이터 모델

```json
{
  "id": 1,
  "semesterId": 1,
  "title": "자료구조",
  "courseCode": "CS201",
  "professor": "김철수",
  "classroom": "공학관 302",
  "credit": 3,
  "difficultyLevel": 4,
  "createdAt": "2026-05-18T10:30:00",
  "updatedAt": "2026-05-18T10:30:00"
}
```

### 필드 설명
- `id`: 수업 고유 ID (자동 생성)
- `semesterId`: 소속 학기 ID
- `title`: 수업명 (필수, 예: "자료구조")
- `courseCode`: 과목 코드 (최대 8자, 예: "CS201")
- `professor`: 교수명 (최대 50자)
- `classroom`: 강의실 (최대 50자)
- `credit`: 학점
- `difficultyLevel`: 난이도 (1~5)
- `createdAt`: 생성 일시 (자동 기록)
- `updatedAt`: 수정 일시 (자동 기록)

## API 엔드포인트

| 기능 | 메서드 | 엔드포인트 | 설명 |
|------|--------|-----------|------|
| 생성 | POST | `/api/semesters/{semesterId}/courses` | 학기에 속한 수업 생성 |
| 목록 조회 | GET | `/api/semesters/{semesterId}/courses` | 학기별 수업 목록 조회 |
| 상세 조회 | GET | `/api/courses/{courseId}` | 특정 수업 정보 조회 |
| 수정 | PATCH | `/api/courses/{courseId}` | 수업 정보 수정 |
| 삭제 | DELETE | `/api/courses/{courseId}` | 수업 삭제 |

## Postman 테스트 데이터

### 사전 준비
- 먼저 Semester API로 학기를 생성하고 ID를 확인하세요.
- 아래 예시에서는 `semesterId=1`, `courseId=1`을 사용합니다.

### 1. 수업 생성 (POST)
```bash
POST http://localhost:8080/api/semesters/1/courses
Content-Type: application/json

{
  "title": "자료구조",
  "courseCode": "CS201",
  "professor": "김철수",
  "classroom": "공학관 302",
  "credit": 3,
  "difficultyLevel": 4
}
```

**응답 예시 (201 Created)**
```json
{
  "id": 1,
  "semesterId": 1,
  "title": "자료구조",
  "courseCode": "CS201",
  "professor": "김철수",
  "classroom": "공학관 302",
  "credit": 3,
  "difficultyLevel": 4,
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
    "title": "자료구조",
    "courseCode": "CS201",
    "professor": "김철수",
    "classroom": "공학관 302",
    "credit": 3,
    "difficultyLevel": 4,
    "createdAt": "2026-05-18T10:35:00",
    "updatedAt": "2026-05-18T10:35:00"
  },
  {
    "id": 2,
    "semesterId": 1,
    "title": "데이터베이스",
    "courseCode": "CS305",
    "professor": "이영희",
    "classroom": "공학관 305",
    "credit": 3,
    "difficultyLevel": 3,
    "createdAt": "2026-05-18T10:40:00",
    "updatedAt": "2026-05-18T10:40:00"
  }
]
```

### 3. 수업 상세 조회 (GET)
```bash
GET http://localhost:8080/api/courses/1
```

**응답 예시 (200 OK)**
```json
{
  "id": 1,
  "semesterId": 1,
  "title": "자료구조",
  "courseCode": "CS201",
  "professor": "김철수",
  "classroom": "공학관 302",
  "credit": 3,
  "difficultyLevel": 4,
  "createdAt": "2026-05-18T10:35:00",
  "updatedAt": "2026-05-18T10:35:00"
}
```

### 4. 수업 정보 수정 (PATCH)
```bash
PATCH http://localhost:8080/api/courses/1
Content-Type: application/json

{
  "title": "자료구조 심화",
  "classroom": "공학관 305",
  "difficultyLevel": 5
}
```

**응답 예시 (200 OK)**
```json
{
  "id": 1,
  "semesterId": 1,
  "title": "자료구조 심화",
  "courseCode": "CS201",
  "professor": "김철수",
  "classroom": "공학관 305",
  "credit": 3,
  "difficultyLevel": 5,
  "createdAt": "2026-05-18T10:35:00",
  "updatedAt": "2026-05-18T11:45:00"
}
```

### 5. 수업 삭제 (DELETE)
```bash
DELETE http://localhost:8080/api/courses/1
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

# 2. 수업 생성
POST http://localhost:8080/api/semesters/1/courses
{
  "title": "자료구조",
  "courseCode": "CS201",
  "professor": "김철수",
  "classroom": "공학관 302",
  "credit": 3,
  "difficultyLevel": 4
}
# 응답에서 id=1 확인

# 3. 수업 목록 조회
GET http://localhost:8080/api/semesters/1/courses

# 4. 수업 상세 조회
GET http://localhost:8080/api/courses/1

# 5. 수업 수정
PATCH http://localhost:8080/api/courses/1
{
  "classroom": "공학관 305",
  "difficultyLevel": 5
}

# 6. 수업 삭제
DELETE http://localhost:8080/api/courses/1

# 7. 학기 삭제 (Semester API)
DELETE http://localhost:8080/api/semesters/1
```

## 에러 응답

- **404 Not Found**: 존재하지 않는 학기 또는 수업 ID
- **400 Bad Request**: 잘못된 요청 형식 또는 유효하지 않은 필드 값
- **500 Internal Server Error**: 서버 오류

## 주의사항

- 수업 생성/목록 조회는 `semesterId`를 경로에 포함하며, 해당 학기가 존재해야 합니다.
- 단건 상세/수정/삭제는 `courseId`만으로 식별합니다.
- 난이도 `difficultyLevel`은 1~5 범위의 값입니다. 별도 난이도 엔드포인트 없이 수업 생성/수정으로 설정합니다.
- 수업명 `title`은 필수입니다.
- PATCH 요청 시 null 값인 필드는 수정되지 않습니다.
- 경로에서 `{semesterId}`와 `{courseId}`는 각각 학기 ID와 수업 ID로 치환해야 합니다.

## EDWARD 동기화

EDWARD 시간표 import 시 수업(Course)과 학점(`credit`)이 자동 생성·갱신됩니다. [sync/README.md](../sync/README.md) 참고.
