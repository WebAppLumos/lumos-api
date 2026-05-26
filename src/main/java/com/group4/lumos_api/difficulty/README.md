# Difficulty API

난이도(Difficulty) 정보를 관리하는 API입니다.

## 개요

난이도는 특정 수업의 체감 난이도 값을 나타냅니다. 각 수업은 하나의 난이도 정보를 가질 수 있으며, 시간표에 배치된 수업들의 평균 난이도도 조회할 수 있습니다.

## 데이터 모델

### 수업 난이도

```json
{
  "id": 1,
  "courseId": 1,
  "level": 4,
  "createdAt": "2026-05-22T10:30:00",
  "updatedAt": "2026-05-22T10:30:00"
}
```

### 시간표 평균 난이도

```json
{
  "timetableId": 1,
  "courseCount": 2,
  "ratedCourseCount": 2,
  "averageLevel": 4.5
}
```

### 필드 설명
- `id`: 난이도 고유 ID (자동 생성)
- `courseId`: 난이도를 설정한 수업 ID
- `level`: 난이도 값 (1~5)
- `createdAt`: 생성 일시 (자동 기록)
- `updatedAt`: 수정 일시 (자동 기록)
- `timetableId`: 평균 난이도를 조회한 시간표 ID
- `courseCount`: 시간표에 배치된 전체 수업 수
- `ratedCourseCount`: 난이도가 설정된 수업 수
- `averageLevel`: 난이도가 설정된 수업들의 평균값

## API 엔드포인트

| 기능 | 메서드 | 엔드포인트 | 설명 |
|------|--------|-----------|------|
| 설정 | POST | `/api/semesters/{semesterId}/courses/{courseId}/difficulty` | 수업 난이도 설정 |
| 조회 | GET | `/api/semesters/{semesterId}/courses/{courseId}/difficulty` | 특정 수업 난이도 조회 |
| 시간표 평균 조회 | GET | `/api/semesters/{semesterId}/timetables/{timetableId}/difficulty` | 시간표 내 수업들의 평균 난이도 조회 |

## Postman 테스트 데이터

### 사전 준비
- 먼저 Semester API로 학기를 생성하고 ID를 확인하세요.
- Course API로 수업을 생성하고 ID를 확인하세요.
- Timetable API로 시간표를 생성하고 ID를 확인하세요.
- Entry API로 시간표에 수업을 배치하세요.
- 아래 예시에서는 `semesterId=1`, `courseId=1`, `timetableId=1`을 사용합니다.

### 1. 난이도 설정 (POST)
```bash
POST http://localhost:8080/api/semesters/1/courses/1/difficulty
Content-Type: application/json

{
  "level": 4
}
```

**응답 예시 (201 Created)**
```json
{
  "id": 1,
  "courseId": 1,
  "level": 4,
  "createdAt": "2026-05-22T10:30:00",
  "updatedAt": "2026-05-22T10:30:00"
}
```

### 2. 난이도 조회 (GET)
```bash
GET http://localhost:8080/api/semesters/1/courses/1/difficulty
```

**응답 예시 (200 OK)**
```json
{
  "id": 1,
  "courseId": 1,
  "level": 4,
  "createdAt": "2026-05-22T10:30:00",
  "updatedAt": "2026-05-22T10:30:00"
}
```

### 3. 시간표 평균 난이도 조회 (GET)
```bash
GET http://localhost:8080/api/semesters/1/timetables/1/difficulty
```

**응답 예시 (200 OK)**
```json
{
  "timetableId": 1,
  "courseCount": 2,
  "ratedCourseCount": 2,
  "averageLevel": 4.5
}
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

# 4. 시간표에 수업 배치 (Entry API)
POST http://localhost:8080/api/semesters/1/timetables/1/entries
{
  "courseId": 1,
  "dayOfWeek": 1,
  "startTime": "09:00:00",
  "endTime": "10:15:00"
}

# 5. 수업 난이도 설정
POST http://localhost:8080/api/semesters/1/courses/1/difficulty
{
  "level": 4
}

# 6. 수업 난이도 조회
GET http://localhost:8080/api/semesters/1/courses/1/difficulty

# 7. 시간표 평균 난이도 조회
GET http://localhost:8080/api/semesters/1/timetables/1/difficulty
```

## 에러 응답

- **404 Not Found**: 존재하지 않는 학기, 수업, 시간표 또는 난이도 ID
- **400 Bad Request**: 잘못된 요청 형식 또는 유효하지 않은 난이도 값
- **500 Internal Server Error**: 서버 오류

## 주의사항

- 난이도 설정 시 `courseId`에 해당하는 수업이 요청한 학기에 속해야 합니다.
- 난이도 값 `level`은 1 이상 5 이하이어야 합니다.
- 같은 수업에 다시 POST 요청을 보내면 기존 난이도 정보가 갱신됩니다.
- 시간표 평균 난이도는 시간표에 배치된 수업 중 난이도가 설정된 수업만 평균 계산에 포함합니다.
- 난이도가 설정된 수업이 없으면 `averageLevel`은 `0.0`으로 응답합니다.
- 경로에서 `{semesterId}`, `{courseId}`, `{timetableId}`는 각각 학기 ID, 수업 ID, 시간표 ID로 치환해야 합니다.
