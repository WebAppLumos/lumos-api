# 📊 Lumos API - Previous Semester Scores Module

사용자의 직전 학기 성적 정보를 관리하는 API입니다. 학기별 평점(GPA)을 기록하고 조회, 수정, 삭제할 수 있는 기능을 제공합니다.

---

## 📂 패키지 구조

```text
com.group4.lumos_api.previous_semester_scores/
├── controller/
│   └── PreviousSemesterScoresController  # 성적 관리 엔드포인트
├── service/
│   └── PreviousSemesterScoresService     # 성적 관련 비즈니스 로직
├── repository/
│   └── PreviousSemesterScoresRepository  # DB 접근 (JPA)
├── entity/
│   └── PreviousSemesterScores            # 성적 DB 엔티티 (PK: grade_id)
└── dto/
    ├── ScoreRequestDto                   # 요청 데이터 규격 (평점, 연도, 학기)
    └── ScoreResponseDto                  # 응답 데이터 규격
```

---

## 🚀 API 명세 (Endpoints)

| 기능명 | 엔드포인트 | Method | 설명 |
| :--- | :--- | :--- | :--- |
| 사용자별 성적 조회 | `/api/users/{userId}/previous-semester-scores` | GET | 특정 사용자의 전체 성적 기록 조회 |
| 성적 추가 | `/api/users/{userId}/previous-semester-scores` | POST | 특정 사용자의 새로운 성적 기록 추가 |
| 성적 수정 | `/api/previous-semester-scores/{scoreId}` | PATCH | 기존 성적 기록(평점, 연도, 학기 등) 수정 |
| 성적 삭제 | `/api/previous-semester-scores/{scoreId}` | DELETE | 특정 성적 기록 삭제 |

---

## 📋 데이터 모델 상세

### 성적 정보 (Score)
- **gradeId**: 성적 기록 고유 식별자 (Long)
- **userId**: 사용자 식별자 (String)
- **score**: 해당 학기 평점 (Double, 예: 4.5)
- **year**: 해당 연도 (LocalDate)
- **semester**: 학기 정보 (String, 예: "1학기", "겨울학기")

---

## 🚀 API 사용 예시 (Examples)

### 1. 성적 추가 (POST)
- **Endpoint**: `POST /api/users/{userId}/previous-semester-scores`
- **Request Body**:
```json
{
  "score": 4.25,
  "year": "2024-03-01",
  "semester": "1학기"
}
```
- **Response**: `200 OK`
```json
{
  "gradeId": 1,
  "userId": "user_123",
  "score": 4.25,
  "year": "2024-03-01",
  "semester": "1학기",
  "createdAt": "2024-06-14T10:00:00",
  "updatedAt": "2024-06-14T10:00:00"
}
```

### 2. 특정 사용자의 성적 목록 조회 (GET)
- **Endpoint**: `GET /api/users/{userId}/previous-semester-scores`
- **Response**: `200 OK`
```json
[
  {
    "gradeId": 1,
    "userId": "user_123",
    "score": 4.25,
    "year": "2024-03-01",
    "semester": "1학기",
    "createdAt": "2024-06-14T10:00:00",
    "updatedAt": "2024-06-14T10:00:00"
  }
]
```

### 3. 성적 정보 수정 (PATCH)
- **Endpoint**: `PATCH /api/previous-semester-scores/{scoreId}`
- **Request Body**:
```json
{
  "score": 4.4,
  "semester": "여름학기"
}
```
- **Response**: `200 OK`
```json
{
  "gradeId": 1,
  "userId": "user_123",
  "score": 4.4,
  "year": "2024-03-01",
  "semester": "여름학기",
  "createdAt": "2024-06-14T10:00:00",
  "updatedAt": "2024-06-14T11:30:00"
}
```

### 4. 성적 삭제 (DELETE)
- **Endpoint**: `DELETE /api/previous-semester-scores/{scoreId}`
- **Response**: `204 No Content`

---

## ⚠️ 주의사항

1. **사용자 연동**: 성적 추가 및 조회 시 경로 변수(`{userId}`)로 전달된 사용자가 시스템에 존재해야 합니다.
2. **날짜 형식**: `year` 필드는 `YYYY-MM-DD` 형식으로 전달해야 하며, 내부적으로 해당 연도의 정보를 저장하는 데 사용됩니다.
3. **부분 수정**: `PATCH` 메서드를 통해 필요한 필드(`score`, `year`, `semester`)만 선택적으로 수정할 수 있습니다.
