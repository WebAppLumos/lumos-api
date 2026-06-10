# User API

인증된 사용자 본인의 정보를 관리하는 API입니다. 대상 사용자는 경로가 아니라 `Authorization: Bearer <Firebase ID Token>` 헤더에서 식별합니다.

> 신규 사용자 등록은 `POST /api/auth/login`(로그인 시 자동 등록/동기화)에서 처리합니다. User API는 본인(`/me`) 전용입니다.

## 엔드포인트 요약

모든 요청에 `Authorization: Bearer <Firebase ID Token>` 헤더가 필요합니다.

| 기능 | 메서드 | 엔드포인트 | 설명 |
|---|---|---|---|
| 내 정보 조회 | GET | `/api/users/me` | 현재 로그인한 사용자 정보를 조회합니다. |
| 내 정보 수정 | PATCH | `/api/users/me` | 현재 로그인한 사용자 정보를 수정합니다. |
| 회원 탈퇴 | DELETE | `/api/users/me` | 현재 로그인한 사용자 계정을 삭제합니다. |

## 데이터 모델 (JSON)

### 내 정보 수정 요청 (Request)
```json
{
  "email": "student@example.com",
  "name": "홍길동",
  "phoneNumber": "010-1234-5678",
  "department": "컴퓨터공학과",
  "grade": 3,
  "studentNumber": "2024001"
}
```

### 내 정보 응답 (Response)
```json
{
  "userId": "firebase-uid",
  "email": "student@example.com",
  "name": "홍길동",
  "phoneNumber": "010-1234-5678",
  "department": "컴퓨터공학과",
  "grade": 3,
  "studentNumber": "2024001"
}
```

## 테스트 가이드

### 1. 내 정보 조회 (GET)
- URL: `http://localhost:8080/api/users/me`
- Header: `Authorization: Bearer <Firebase ID Token>`

### 2. 내 정보 수정 (PATCH)
- URL: `http://localhost:8080/api/users/me`
- Header: `Authorization: Bearer <Firebase ID Token>`
- Body: 위 Request JSON 예시 참고
