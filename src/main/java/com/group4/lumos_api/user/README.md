# User API

사용자 기본 정보를 관리하는 API입니다.

## 엔드포인트 요약

| 기능 | 메서드 | 엔드포인트 | 설명 |
|---|---|---|---|
| 사용자 생성 | POST | `/api/users` | 새로운 사용자를 등록합니다. |
| 사용자 목록 조회 | GET | `/api/users` | 등록된 모든 사용자 목록을 조회합니다. |
| 사용자 상세 조회 | GET | `/api/users/{userId}` | 특정 사용자의 상세 정보를 조회합니다. |
| 사용자 정보 수정 | PATCH | `/api/users/{userId}` | 특정 사용자의 정보를 일부 수정합니다. |
| 사용자 삭제 | DELETE | `/api/users/{userId}` | 특정 사용자 정보를 삭제합니다. |

## 데이터 모델 (JSON)

### 사용자 생성/수정 요청 (Request)
```json
{
  "userId": "student_01",
  "email": "student@example.com",
  "name": "홍길동",
  "phoneNumber": "010-1234-5678",
  "department": "컴퓨터공학과",
  "grade": 3,
  "studentNumber": "2024001"
}
```

### 사용자 상세 응답 (Response)
```json
{
  "userId": "student_01",
  "email": "student@example.com",
  "name": "홍길동",
  "phoneNumber": "010-1234-5678",
  "department": "컴퓨터공학과",
  "grade": 3,
  "studentNumber": "2024001"
}
```

## 테스트 가이드

### 1. 사용자 생성 (POST)
- URL: `http://localhost:8080/api/users`
- Body: 위 Request JSON 예시 참고

### 2. 사용자 상세 조회 (GET)
- URL: `http://localhost:8080/api/users/student_01`
