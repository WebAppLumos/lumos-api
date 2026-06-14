# Language Exams Module

이 모듈은 학생의 공인외국어 시험 성적(TOEIC, TOEFL, OPIC 등)을 관리하는 기능을 제공합니다.

## 주요 기능
- 학생별 성적 조회
- 새로운 성적 추가
- 기존 성적 정보 수정
- 성적 삭제

## 데이터 구조 (Entity)
- **exam_id**: PK (Auto Increment)
- **exam_category**: 시험 종류 (예: TOEIC, OPIC)
- **score**: 취득 점수 또는 등급
- **exam_date**: 응시 일자
- **year**: 응시 연도
- **semester**: 응시 학기
- **expiry_date**: 성적 만료일
- **student_id**: FK (Students 엔티티 참조)

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users/{userId}/language-exams` | 특정 학생의 모든 성적 조회 |
| POST | `/api/users/{userId}/language-exams` | 새로운 성적 등록 |
| PATCH | `/api/language-exams/{examId}` | 성적 정보 수정 |
| DELETE | `/api/language-exams/{examId}` | 성적 삭제 |

## API 사용 예시 (Examples)

### 1. 새로운 성적 등록 (POST)
- **Endpoint**: `POST /api/users/{userId}/language-exams`
- **Request Body**:
```json
{
  "examCategory": "TOEIC",
  "score": "950",
  "examDate": "2024-05-10",
  "year": 2024,
  "semester": "1학기",
  "expiryDate": "2026-05-10"
}
```
- **Response**:
```json
{
  "examId": 1,
  "examCategory": "TOEIC",
  "score": "950",
  "examDate": "2024-05-10",
  "year": 2024,
  "semester": "1학기",
  "expiryDate": "2026-05-10",
  "userId": "user_id_example"
}
```

### 2. 학생별 성적 목록 조회 (GET)
- **Endpoint**: `GET /api/users/{userId}/language-exams`
- **Response**:
```json
[
  {
    "examId": 1,
    "examCategory": "TOEIC",
    "score": "950",
    "examDate": "2024-05-10",
    "year": 2024,
    "semester": "1학기",
    "expiryDate": "2026-05-10",
    "userId": "user_id_example"
  }
]
```

## 패키지 구조
- `controller`: REST API 엔드포인트 정의
- `service`: 비즈니스 로직 처리 및 DTO 변환
- `repository`: Spring Data JPA를 이용한 데이터베이스 접근
- `entity`: 데이터베이스 테이블 매핑 클래스
- `dto`: 데이터 전송 객체 (Request/Response)
