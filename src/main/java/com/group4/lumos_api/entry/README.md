# Entry API

수업 배치(Entry) 정보를 관리하는 API입니다.

## 개요

수업 배치는 특정 시간표에 수업을 배치한 정보를 나타냅니다. Entry는 시간표와 수업에 모두 종속되며, 생성 시 시간표 ID와 수업 ID가 모두 유효해야 합니다.

## 데이터 모델

```json
{
  "id": 1,
  "timetableId": 1,
  "courseId": 1,
  "courseTitle": "자료구조",
  "classroom": "공학관 302",
  "professor": "김철수",
  "dayOfWeek": 1,
  "startTime": "09:00:00",
  "endTime": "10:15:00",
  "createdAt": "2026-05-22T10:30:00",
  "updatedAt": "2026-05-22T10:30:00"
}
```

### 필드 설명
- `id`: 수업 배치 고유 ID (자동 생성)
- `timetableId`: 배치할 시간표 ID (필수)
- `courseId`: 배치할 수업 ID (필수)
- `courseTitle`: 수업명
- `classroom`: 강의실
- `professor`: 담당 교수
- `dayOfWeek`: 요일 값 (예: 1=월요일, 2=화요일)
- `startTime`: 수업 시작 시간 (ISO 8601 시간 형식: HH:mm:ss)
- `endTime`: 수업 종료 시간 (ISO 8601 시간 형식: HH:mm:ss)
- `createdAt`: 생성 일시 (자동 기록)
- `updatedAt`: 수정 일시 (자동 기록)

## API 엔드포인트

| 기능 | 메서드 | 엔드포인트 | 설명 |
|------|--------|-----------|------|
| 생성 | POST | `/api/semesters/{semesterId}/timetables/{timetableId}/entries` | 시간표에 수업 배치 |
| 목록 조회 | GET | `/api/semesters/{semesterId}/timetables/{timetableId}/entries` | 시간표에 배치된 수업 목록 조회 |
| 삭제 | DELETE | `/api/semesters/{semesterId}/timetables/{timetableId}/entries/{entryId}` | 시간표에서 수업 제거 |

## Postman 테스트 데이터

### 사전 준비
- 먼저 Semester API로 학기를 생성하고 ID를 확인하세요.
- Course API로 수업을 생성하고 ID를 확인하세요.
- Timetable API로 시간표를 생성하고 ID를 확인하세요.
- 아래 예시에서는 `semesterId=1`, `timetableId=1`, `courseId=1`, `entryId=1`을 사용합니다.

### 1. 수업 배치 생성 (POST)
```bash
POST http://localhost:8080/api/semesters/1/timetables/1/entries
Content-Type: application/json

{
  "courseId": 1,
  "dayOfWeek": 1,
  "startTime": "09:00:00",
  "endTime": "10:15:00"
}
```

**응답 예시 (201 Created)**
```json
{
  "id": 1,
  "timetableId": 1,
  "courseId": 1,
  "courseTitle": "자료구조",
  "classroom": "공학관 302",
  "professor": "김철수",
  "dayOfWeek": 1,
  "startTime": "09:00:00",
  "endTime": "10:15:00",
  "createdAt": "2026-05-22T10:30:00",
  "updatedAt": "2026-05-22T10:30:00"
}
```

### 2. 수업 배치 목록 조회 (GET)
```bash
GET http://localhost:8080/api/semesters/1/timetables/1/entries
```

**응답 예시 (200 OK)**
```json
[
  {
    "id": 1,
    "timetableId": 1,
    "courseId": 1,
    "courseTitle": "자료구조",
    "classroom": "공학관 302",
    "professor": "김철수",
    "dayOfWeek": 1,
    "startTime": "09:00:00",
    "endTime": "10:15:00",
    "createdAt": "2026-05-22T10:30:00",
    "updatedAt": "2026-05-22T10:30:00"
  },
  {
    "id": 2,
    "timetableId": 1,
    "courseId": 2,
    "courseTitle": "운영체제",
    "classroom": "공학관 305",
    "professor": "이영희",
    "dayOfWeek": 3,
    "startTime": "13:30:00",
    "endTime": "14:45:00",
    "createdAt": "2026-05-22T10:40:00",
    "updatedAt": "2026-05-22T10:40:00"
  }
]
```

### 3. 수업 배치 삭제 (DELETE)
```bash
DELETE http://localhost:8080/api/semesters/1/timetables/1/entries/1
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

# 3. 시간표 생성 (Timetable API)
POST http://localhost:8080/api/semesters/1/timetables
{
  "title": "2026-1학기 기본 시간표"
}
# 응답에서 id=1 확인

# 4. 시간표에 수업 배치
POST http://localhost:8080/api/semesters/1/timetables/1/entries
{
  "courseId": 1,
  "dayOfWeek": 1,
  "startTime": "09:00:00",
  "endTime": "10:15:00"
}
# 응답에서 id=1 확인

# 5. 수업 배치 목록 조회
GET http://localhost:8080/api/semesters/1/timetables/1/entries

# 6. 수업 배치 삭제
DELETE http://localhost:8080/api/semesters/1/timetables/1/entries/1
```

## 에러 응답

- **404 Not Found**: 존재하지 않는 학기, 시간표, 수업 또는 수업 배치 ID
- **400 Bad Request**: 잘못된 요청 형식 또는 유효하지 않은 필드 값
- **500 Internal Server Error**: 서버 오류

## 주의사항

- 수업 배치 생성 시 `timetableId`에 해당하는 시간표가 요청한 학기에 속해야 합니다.
- 수업 배치 생성 시 `courseId`에 해당하는 수업도 요청한 학기에 속해야 합니다.
- 같은 시간표에 동일한 수업을 중복 배치할 수 없습니다.
- `dayOfWeek`, `startTime`, `endTime`은 현재 선택 입력값이지만 시간표 화면 표시를 위해 함께 전달하는 것을 권장합니다.
- 경로에서 `{semesterId}`, `{timetableId}`, `{entryId}`는 각각 학기 ID, 시간표 ID, 수업 배치 ID로 치환해야 합니다.
