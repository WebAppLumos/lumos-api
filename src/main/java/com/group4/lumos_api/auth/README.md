# Auth API

Firebase Authentication ID token을 검증한 뒤 Lumos `users` 테이블에 사용자를 등록하거나 로그인 처리하는 API입니다.

## Directory

- `config/FirebaseConfig.java`: Firebase Admin SDK 초기화
- `controller/AuthController.java`: `/api/auth` 엔드포인트
- `dto/*`: 인증 요청/응답 DTO
- `service/AuthService.java`: Firebase 토큰 검증, 사용자 등록/갱신, 토큰 폐기/재발급 처리

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

### Login

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

```json
{
  "accessToken": "firebase-custom-token",
  "tokenType": "FirebaseCustomToken",
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

### Logout

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

### Refresh

```http
POST /api/auth/refresh
Content-Type: application/json
```

Firebase ID token을 검증하고 새 Firebase custom token을 발급합니다.

#### Request

```json
{
  "idToken": "firebase-id-token"
}
```

#### Response

```json
{
  "accessToken": "firebase-custom-token",
  "tokenType": "FirebaseCustomToken",
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
- 잘못된 ID token은 `401 Unauthorized`로 실패합니다.
- `logout`은 Firebase Admin SDK의 `revokeRefreshTokens(uid)`를 사용합니다.
- Firebase ID token 자체의 갱신은 일반적으로 클라이언트 Firebase SDK가 담당합니다. 이 API의 `refresh`는 서버에서 Firebase custom token을 새로 발급하는 용도입니다.
