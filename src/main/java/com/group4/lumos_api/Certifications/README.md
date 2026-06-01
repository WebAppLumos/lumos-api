# Certification API

자격증 정보를 관리하는 API입니다.

## 개요

자격증은 특정 학생에게 속하는 자격 취득 정보를 나타냅니다. 자격증 생성 시에는 반드시 학생을 지정해야 합니다.

## 데이터 모델

```json
{
  "certId": 1,
  "studentId": 1,
  "certName": "정보처리기사",
  "issueDate": "2023-05-20"
}
```

### 필드 설명
- `certId`: 자격증 고유 ID (자동 생성)
- `studentId`: 소속 학생 ID (필수)
- `certName`: 자격증명 (예: "정보처리기사", "SQLD")
- `issueDate`: 취득 일자 (예: "2023-05-20")
