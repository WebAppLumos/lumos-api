# Certification API

자격증 정보를 관리하는 API입니다.

## 개요

자격증은 특정 학생에게 속하는 자격 취득 정보를 나타냅니다. 자격증 생성 시에는 반드시 학생을 지정해야 합니다.

## 데이터 구조 (Entity)
- **cert_id**: PK (Auto Increment)
- **cert_name**: 자격증명 (예: "정보처리기사", "SQLD")
- **issue_date**: 취득 일자 (예: "2023-05-20")
- **user_id**: FK (Users 엔티티 참조)

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users/{userId}/certifications` | 특정 학생의 모든 자격증 조회 |
| POST | `/api/users/{userId}/certifications` | 새로운 자격증 등록 |
| PATCH | `/api/certifications/{certId}` | 자격증 정보 수정 |
| DELETE | `/api/certifications/{certId}` | 자격증 삭제 |

## API 사용 예시 (Examples)

### 1. 새로운 자격증 등록 (POST)
- **Endpoint**: `POST /api/users/{userId}/certifications`
- **Request Body**:
```json
{
  "certName": "정보처리기사",
  "issueDate": "2023-05-20"
}
```
- **Response**:
```json
{
  "certId": 1,
  "certName": "정보처리기사",
  "issueDate": "2023-05-20",
  "userId": "user_id_example"
}
```

### 2. 학생별 자격증 목록 조회 (GET)
- **Endpoint**: `GET /api/users/{userId}/certifications`
- **Response**:
```json
[
  {
    "certId": 1,
    "certName": "정보처리기사",
    "issueDate": "2023-05-20",
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
