# 🎓 Lumos API - Recent Semester Credits Module

가장 최근 학기의 총 취득학점 정보를 제공하는 API 모듈입니다.

---

## 📂 패키지 구조

```text
com.group4.lumos_api.recent_semester_credits/
├── controller/
│   └── RecentSemesterCreditsController  # 최근 학기 학점 조회 엔드포인트
└── service/
    └── RecentSemesterCreditsService     # 최근 학기 판별 및 학점 합산 로직
```

---

## 🚀 API 명세 (Endpoints)

| 기능명 | 엔드포인트 | Method | 설명 |
| :--- | :--- | :--- | :--- |
| 최근 학기 총 학점 조회 | `/api/recent-semester/total-credits` | GET | 사용자의 가장 최근 학기(시작일 기준)의 총 학점 합계를 반환 |

---

## 📋 로직 설명

1.  **학기 판별**: 사용자가 소유한 모든 학기 중 `startDate`(시작일)가 가장 늦은 학기를 "가장 최근 학기"로 간주합니다.
2.  **학점 합산**: 해당 학기에 등록된 모든 수업(`Course`)의 `credit` 필드 값을 합산합니다.
    *   수업에 학점이 입력되지 않은 경우(`null`) 0으로 처리합니다.
    *   학기 정보가 없는 사용자의 경우 0을 반환합니다.

---

## 🚀 API 사용 예시 (Examples)

### 1. 최근 학기 총 학점 조회 (GET)
- **Endpoint**: `GET /api/recent-semester/total-credits`
- **Response**: `200 OK`
- **Response Body**:
```json
18
```

---

## ⚠️ 참고 사항

1.  **독립성**: 이 모듈은 기존 `Semester` 및 `Course` 모듈의 엔티티와 리포지토리를 활용하지만, 기존 코드를 수정하지 않고 독립적인 패키지로 구성되었습니다.
2.  **보안**: 요청 시 인증된 사용자의 정보를 기반으로 데이터를 조회합니다.
3.  **EDWARD 연동**: 마이페이지 신청 학점 UI는 활성 학기 수업 `credit` 합산을 사용합니다. 본 API는 최근 학기 학점 조회용 보조 엔드포인트입니다.
