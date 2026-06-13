# Auth API

Firebase Authentication ID token을 검증한 뒤 Lumos `users` 테이블에 사용자를 등록하거나 로그인 처리하는 API입니다.

## API 엔드포인트

| 기능 | 도메인 | 엔드포인트 | 메서드 | 설명 |
|------|--------|-----------|--------|------|
| 로그인 | auth | `/api/auth/login` | POST | Firebase ID 토큰 검증 후 회원 등록/로그인. 서버는 별도 토큰을 발급하지 않으며, 클라이언트는 동일 ID 토큰을 이후 요청에 사용 |
| 로그아웃 | auth | `/api/auth/logout` | POST | 요청 body의 Firebase ID 토큰으로 사용자를 식별한 뒤 refresh token을 폐기하여 로그아웃 처리 |
| 토큰 검증 | auth | `/api/auth/refresh` | POST | Firebase ID 토큰 재검증(폐기 여부 포함) 후 사용자 프로필 반환. 세션 유효성 확인/프로필 동기화 용도이며, 새 토큰을 발급하지 않음 |

> `/api/auth/**` 엔드포인트는 인증 헤더 없이 호출할 수 있습니다. 보호된 API는 `Authorization: Bearer <Firebase ID Token>` 헤더가 필요합니다.

## Directory

- `config/FirebaseConfig.java`: Firebase Admin SDK 초기화
- `controller/AuthController.java`: `/api/auth` 엔드포인트
- `dto/*`: 인증 요청/응답 DTO
- `service/AuthService.java`: Firebase 토큰 검증, 사용자 등록/갱신, 토큰 폐기 처리

## 인증 모델

이 API는 서버가 자체 토큰을 발급하지 않습니다. 클라이언트는 Firebase 클라이언트 SDK로 발급받은 **ID 토큰**을 보호된 엔드포인트(`/api/auth/**` 외 전체)의 요청 헤더에 그대로 사용합니다.

```http
Authorization: Bearer <Firebase ID Token>
```

서버 필터가 매 요청마다 ID 토큰을 검증하여 사용자(uid)를 식별합니다. ID 토큰 만료 시 갱신은 Firebase 클라이언트 SDK가 담당합니다.

## Firebase Setup

서버 실행 전에 Firebase Admin SDK 자격증명을 아래 방법 중 하나로 설정해야 합니다.

### 1. Service account JSON 문자열

```powershell
$env:FIREBASE_SERVICE_ACCOUNT_JSON='{"type":"service_account", ... }'
```

### 2. Service account JSON 파일 경로

```powershell
$env:FIREBASE_SERVICE_ACCOUNT_PATH='C:\path\to\firebase-service-account.json'
```

### 3. Application Default Credentials

위 환경변수가 없으면 `GoogleCredentials.getApplicationDefault()`를 사용합니다.

## Endpoints

### 로그인 — `POST /api/auth/login`

```http
POST /api/auth/login
Content-Type: application/json
```

Firebase ID token을 검증하고, Firebase UID 기준으로 사용자를 등록하거나 기존 사용자 정보를 갱신합니다.

#### Request

```json
{
  "idToken": "firebase-id-token",
  "name": "홍길동",
  "phoneNumber": "010-1234-5678",
  "department": "컴퓨터공학과",
  "grade": 3,
  "studentNumber": "2024001"
}
```

#### Required

- `idToken`: Firebase 클라이언트 SDK에서 발급받은 ID token

#### Optional

- `name`
- `phoneNumber`
- `department`
- `grade`
- `studentNumber`

`name`이 없으면 Firebase token의 이름을 사용하고, token에도 이름이 없으면 이메일을 이름으로 사용합니다.

#### Response

서버는 별도 토큰을 발급하지 않습니다. 클라이언트는 로그인에 사용한 Firebase ID 토큰을 이후 요청의 `Authorization: Bearer` 헤더에 그대로 사용합니다.

```json
{
  "user": {
    "userId": "firebase-uid",
    "email": "user@example.com",
    "name": "홍길동",
    "phoneNumber": "010-1234-5678",
    "department": "컴퓨터공학과",
    "grade": 3,
    "studentNumber": "2024001",
    "createdAt": "2026-06-05T02:38:00",
    "updatedAt": "2026-06-05T02:38:00"
  }
}
```

### 로그아웃 — `POST /api/auth/logout`

```http
POST /api/auth/logout
Content-Type: application/json
```

Firebase UID의 refresh token을 폐기합니다. 이후 클라이언트는 다시 로그인해야 합니다.

#### Request

```json
{
  "idToken": "firebase-id-token"
}
```

#### Response

```json
{
  "message": "Logged out successfully"
}
```

### 토큰 검증 — `POST /api/auth/refresh`

```http
POST /api/auth/refresh
Content-Type: application/json
```

Firebase ID token의 유효성(폐기 여부 포함)을 재검증하고 현재 사용자 정보를 반환합니다. 새 토큰을 발급하지 않으며, 세션 유효성 확인/프로필 동기화 용도입니다. (실제 ID 토큰 갱신은 클라이언트 SDK가 담당)

#### Request

```json
{
  "idToken": "firebase-id-token"
}
```

#### Response

```json
{
  "user": {
    "userId": "firebase-uid",
    "email": "user@example.com",
    "name": "홍길동",
    "phoneNumber": "010-1234-5678",
    "department": "컴퓨터공학과",
    "grade": 3,
    "studentNumber": "2024001",
    "createdAt": "2026-06-05T02:38:00",
    "updatedAt": "2026-06-05T02:38:00"
  }
}
```

## Notes

- `users.user_id`에는 Firebase UID가 저장됩니다.
- Firebase token에 이메일이 없으면 로그인 요청은 `400 Bad Request`로 실패합니다.
- 잘못된/누락된 ID token으로 보호된 엔드포인트에 접근하면 `401 Unauthorized`로 실패합니다.
- `logout`은 Firebase Admin SDK의 `revokeRefreshTokens(uid)`를 사용합니다.
- 서버는 자체 액세스 토큰을 발급하지 않습니다. 인증에는 클라이언트가 보유한 Firebase ID 토큰을 그대로 사용하며, 만료 시 갱신은 클라이언트 Firebase SDK가 담당합니다.
