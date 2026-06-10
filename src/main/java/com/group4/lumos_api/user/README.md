# 👤 Lumos API - User Module

이 모듈은 파이어베이스 인증(Firebase Auth) 기반의 사용자 계정 관리 및 프로필 정보를 관리하는 API를 제공합니다.

---

## 🚀 시작하기 (How to Run)

터미널에서 프로젝트 루트 폴더로 이동한 뒤 아래 명령어를 입력하여 서버를 실행합니다:

# Windows (PowerShell)
.\gradlew bootRun

# macOS / Linux
./gradlew bootRun

서버가 실행되면 http://localhost:8080에서 API를 호출할 수 있습니다.

---

## 📂 패키지 구조

com.group4.lumos_api.user/
├── controller/
│   └── UserController       # 사용자 정보 관리 엔드포인트
├── service/
│   └── UserService          # 파이어베이스 UID 기반 유저 비즈니스 로직
├── repository/
│   └── UserRepository       # DB 접근 (JPA)
├── entity/
│   └── Users                # 사용자 DB 엔티티 (PK: user_id)
└── dto/
    ├── UserRequestDto       # 요청 데이터 규격
    └── UserResponseDto      # 응답 데이터 규격 (@JsonIgnore 적용)

---

## 🚀 API 명세 (Endpoints)

| 기능명 | 엔드포인트 | Method | 설명 |
| :--- | :--- | :--- | :--- |
| 사용자 등록 | /api/users | POST | 파이어베이스 UID 기반 계정 생성 |
| 전체 사용자 조회 | /api/users | GET | 시스템 전체 사용자 목록 조회 |
| 내 정보 조회 | /api/users/me | GET | 로그인 사용자 상세 정보 조회 |
| 내 정보 수정 | /api/users/me | PATCH | 이름, 학과 등 텍스트 정보 수정 |
| 프로필 이미지 수정 | /api/users/me/profile-image | PATCH | 프로필 이미지 URL 변경 |
| 알림 설정 조회 | /api/users/me/settings | GET | 사용자 알림 설정 조회 (미구현) |
| 알림 설정 수정 | /api/users/me/settings | PATCH | 사용자 알림 설정 수정 (미구현) |
| 회원 탈퇴 | /api/users/me | DELETE | 사용자 계정 삭제 |

---

## 🔒 데이터 보안 및 모델

- X-User-Id 헤더: 내 정보 관련 API(/me)는 헤더에 반드시 X-User-Id를 포함해야 합니다.
- 필드 보안: 응답 DTO에서 시스템 내부용 필드는 @JsonIgnore로 보호됩니다.

---

## 🧪 테스트 시나리오 (Test Flow)

### 1단계: 신규 사용자 등록 (POST)
{
  "userId": "fb_uid_999",
  "email": "jimin@example.com",
  "name": "박지민",
  "phoneNumber": "010-7777-8888",
  "major": "미디어학과",
  "grade": 1,
  "studentNumber": "2026999"
}
- Endpoint: POST /api/users
- Body: {"userId": "fb_uid_1", "email": "a@a.com", "name": "철수", "phoneNumber": "010-1234-5678", "major": "컴공", "grade": 3, "studentNumber": "2024001"}

### 2단계: 전체 사용자 목록 조회 (GET)
- Endpoint: GET /api/users

### 3단계: 내 정보 조회/수정/이미지변경 (GET/PATCH)
- 조회: GET /api/users/me (Header: X-User-Id: fb_uid_1)
- 정보 수정: PATCH /api/users/me (Body: {"phoneNumber": "010-9999-9999", "grade": 4})
- 이미지 수정: PATCH /api/users/me/profile-image (Body: {"profileImageUrl": "https://url.jpg"})

### 4단계: 알림 설정 조회/수정 (GET/PATCH - 미구현)
- 조회: GET /api/users/me/settings (Header: X-User-Id: fb_uid_1)
- 수정: PATCH /api/users/me/settings (Header: X-User-Id: fb_uid_1, Body: {"alarmEnabled": true})

### 5단계: 회원 탈퇴 (DELETE)
- Endpoint: DELETE /api/users/me (Header: X-User-Id: fb_uid_1)

---

## 🛠️ 미구현 기능 (Future Works)

- 알림 설정 조회 (GET /api/users/me/settings)
- 알림 설정 수정 (PATCH /api/users/me/settings)
  - 엔드포인트는 정의되어 있으나, 현재 서비스 로직은 구현 전입니다.

---

## ⚠️ 주의사항

1. 헤더 필수: /me가 포함된 모든 엔드포인트는 X-User-Id 헤더를 통해 인증 정보를 전달해야 합니다.
2. 프로필 이미지: 텍스트 정보 수정과는 별도의 엔드포인트(/profile-image)를 사용합니다.