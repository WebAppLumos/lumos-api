# Note API

노트(Note) 정보를 관리하는 API입니다.

## 개요

노트는 특정 수업에 종속된 학습 메모입니다. 수업별로 노트를 생성하고, 목록 조회, 상세 조회, 검색, 수정, 삭제, 고정 설정/해제를 할 수 있습니다.

## 데이터 모델

```json
{
  "id": 1,
  "courseId": 1,
  "title": "스택과 큐 정리",
  "content": "스택은 LIFO, 큐는 FIFO 구조이다.",
  "isPinned": true,
  "createdAt": "2026-05-22T10:30:00",
  "updatedAt": "2026-05-22T10:30:00"
}
```

### 필드 설명
- `id`: 노트 고유 ID (자동 생성)
- `courseId`: 노트가 속한 수업 ID (필수)
- `title`: 노트 제목
- `content`: 노트 본문
- `isPinned`: 노트 고정 여부
- `createdAt`: 생성 일시 (자동 기록)
- `updatedAt`: 수정 일시 (자동 기록)

## API 엔드포인트

| 기능 | 메서드 | 엔드포인트 | 설명 |
|------|--------|-----------|------|
| 생성 | POST | `/api/semesters/{semesterId}/courses/{courseId}/notes` | 수업에 새로운 노트 생성 |
| 목록 조회 | GET | `/api/semesters/{semesterId}/courses/{courseId}/notes` | 특정 수업의 노트 목록 조회 |
| 상세 조회 | GET | `/api/semesters/{semesterId}/courses/{courseId}/notes/{noteId}` | 특정 노트 상세 조회 |
| 수정 | PATCH | `/api/semesters/{semesterId}/courses/{courseId}/notes/{noteId}` | 노트 제목/내용 수정 |
| 삭제 | DELETE | `/api/semesters/{semesterId}/courses/{courseId}/notes/{noteId}` | 특정 노트 삭제 |
| 검색 | GET | `/api/semesters/{semesterId}/courses/{courseId}/notes?q={keyword}` | 노트 제목으로 검색 |
| 고정 설정/해제 | PATCH | `/api/semesters/{semesterId}/courses/{courseId}/notes/{noteId}/pin` | 노트 고정 상태 설정 또는 토글 |

## Postman 테스트 데이터

### 사전 준비
- 먼저 Semester API로 학기를 생성하고 ID를 확인하세요.
- Course API로 수업을 생성하고 ID를 확인하세요.
- 아래 예시에서는 `semesterId=1`, `courseId=1`, `noteId=1`을 사용합니다.

### 1. 노트 생성 (POST)
```bash
POST http://localhost:8080/api/semesters/1/courses/1/notes
Content-Type: application/json

{
  "title": "스택과 큐 정리",
  "content": "스택은 LIFO, 큐는 FIFO 구조이다."
}
```

**응답 예시 (201 Created)**
```json
{
  "id": 1,
  "courseId": 1,
  "title": "스택과 큐 정리",
  "content": "스택은 LIFO, 큐는 FIFO 구조이다.",
  "isPinned": false,
  "createdAt": "2026-05-22T10:30:00",
  "updatedAt": "2026-05-22T10:30:00"
}
```

### 2. 노트 목록 조회 (GET)
```bash
GET http://localhost:8080/api/semesters/1/courses/1/notes
```

**응답 예시 (200 OK)**
```json
[
  {
    "id": 1,
    "courseId": 1,
    "title": "스택과 큐 정리",
    "content": "스택은 LIFO, 큐는 FIFO 구조이다.",
    "isPinned": false,
    "createdAt": "2026-05-22T10:30:00",
    "updatedAt": "2026-05-22T10:30:00"
  },
  {
    "id": 2,
    "courseId": 1,
    "title": "트리 순회",
    "content": "전위, 중위, 후위 순회를 비교한다.",
    "isPinned": true,
    "createdAt": "2026-05-22T10:40:00",
    "updatedAt": "2026-05-22T10:40:00"
  }
]
```

### 3. 노트 상세 조회 (GET)
```bash
GET http://localhost:8080/api/semesters/1/courses/1/notes/1
```

**응답 예시 (200 OK)**
```json
{
  "id": 1,
  "courseId": 1,
  "title": "스택과 큐 정리",
  "content": "스택은 LIFO, 큐는 FIFO 구조이다.",
  "isPinned": false,
  "createdAt": "2026-05-22T10:30:00",
  "updatedAt": "2026-05-22T10:30:00"
}
```

### 4. 노트 검색 (GET)
```bash
GET http://localhost:8080/api/semesters/1/courses/1/notes?q=스택
```

**응답 예시 (200 OK)**
```json
[
  {
    "id": 1,
    "courseId": 1,
    "title": "스택과 큐 정리",
    "content": "스택은 LIFO, 큐는 FIFO 구조이다.",
    "isPinned": false,
    "createdAt": "2026-05-22T10:30:00",
    "updatedAt": "2026-05-22T10:30:00"
  }
]
```

### 5. 노트 정보 수정 (PATCH)
```bash
PATCH http://localhost:8080/api/semesters/1/courses/1/notes/1
Content-Type: application/json

{
  "title": "스택과 큐 심화 정리",
  "content": "스택은 LIFO, 큐는 FIFO이며 덱은 양쪽 삽입/삭제가 가능하다."
}
```

**응답 예시 (200 OK)**
```json
{
  "id": 1,
  "courseId": 1,
  "title": "스택과 큐 심화 정리",
  "content": "스택은 LIFO, 큐는 FIFO이며 덱은 양쪽 삽입/삭제가 가능하다.",
  "isPinned": false,
  "createdAt": "2026-05-22T10:30:00",
  "updatedAt": "2026-05-22T11:00:00"
}
```

### 6. 노트 고정 설정 (PATCH)
```bash
PATCH http://localhost:8080/api/semesters/1/courses/1/notes/1/pin
Content-Type: application/json

{
  "isPinned": true
}
```

**응답 예시 (200 OK)**
```json
{
  "id": 1,
  "courseId": 1,
  "title": "스택과 큐 심화 정리",
  "content": "스택은 LIFO, 큐는 FIFO이며 덱은 양쪽 삽입/삭제가 가능하다.",
  "isPinned": true,
  "createdAt": "2026-05-22T10:30:00",
  "updatedAt": "2026-05-22T11:05:00"
}
```

### 7. 노트 삭제 (DELETE)
```bash
DELETE http://localhost:8080/api/semesters/1/courses/1/notes/1
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

# 2. 수업 생성 (Course API)
POST http://localhost:8080/api/semesters/1/courses
{
  "title": "자료구조",
  "courseCode": "CS201",
  "professor": "김철수",
  "classroom": "공학관 302",
  "credit": 3
}
# 응답에서 id=1 확인

# 3. 노트 생성
POST http://localhost:8080/api/semesters/1/courses/1/notes
{
  "title": "스택과 큐 정리",
  "content": "스택은 LIFO, 큐는 FIFO 구조이다."
}
# 응답에서 id=1 확인

# 4. 노트 목록 조회
GET http://localhost:8080/api/semesters/1/courses/1/notes

# 5. 노트 검색
GET http://localhost:8080/api/semesters/1/courses/1/notes?q=스택

# 6. 노트 상세 조회
GET http://localhost:8080/api/semesters/1/courses/1/notes/1

# 7. 노트 수정
PATCH http://localhost:8080/api/semesters/1/courses/1/notes/1
{
  "content": "스택, 큐, 덱의 차이를 함께 정리한다."
}

# 8. 노트 고정
PATCH http://localhost:8080/api/semesters/1/courses/1/notes/1/pin
{
  "isPinned": true
}

# 9. 노트 삭제
DELETE http://localhost:8080/api/semesters/1/courses/1/notes/1
```

## 에러 응답

- **404 Not Found**: 존재하지 않는 학기, 수업 또는 노트 ID
- **400 Bad Request**: 잘못된 요청 형식 또는 유효하지 않은 필드 값
- **500 Internal Server Error**: 서버 오류

## 주의사항

- 노트 생성 시 `courseId`에 해당하는 수업이 요청한 학기에 속해야 합니다.
- 노트 제목 `title`은 최대 100자까지 입력할 수 있습니다.
- 노트 목록은 고정된 노트가 먼저 표시되고, 이후 수정 일시 기준 내림차순으로 정렬됩니다.
- 검색 API는 `q` 파라미터가 없으면 전체 목록 조회와 동일하게 동작합니다.
- 현재 검색은 노트 제목 기준으로 수행됩니다.
- 고정 설정/해제 요청에서 `isPinned`를 생략하면 현재 고정 상태가 토글됩니다.
- PATCH 요청 시 null 값인 필드는 수정되지 않습니다.
- 경로에서 `{semesterId}`, `{courseId}`, `{noteId}`는 각각 학기 ID, 수업 ID, 노트 ID로 치환해야 합니다.
