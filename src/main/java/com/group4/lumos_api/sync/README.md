# Sync API (학사 연동)

계명대학교 **EDWARD**·**CTL** 포털에서 수집한 학사 데이터를 Lumos DB에 저장하는 API 모듈입니다.  
브라우저 **Lumos Sync Chrome 확장**이 SSO 세션으로 데이터를 가져온 뒤, 아래 import 엔드포인트를 호출합니다.

---

## 패키지 구조

```text
com.group4.lumos_api.sync/
├── controller/
│   ├── ProfileSyncController      # 학적 정보 import
│   ├── TimetableSyncController    # 시간표 import (+ 레거시 EDWARD 자격증명 sync)
│   ├── GradeSyncController        # 성적 import
│   └── AssignmentSyncController   # CTL 과제 import
├── service/
│   ├── ProfileSyncService
│   ├── TimetableSyncService
│   ├── GradeSyncService
│   └── AssignmentSyncService
├── parser/                        # SSV, MML 파서
├── client/                        # EDWARD 세션 클라이언트
├── dto/                           # 요청/응답 DTO
└── model/                         # 파싱 중간 모델
```

---

## API 엔드포인트

모든 엔드포인트는 `Authorization: Bearer <Firebase ID Token>` 헤더가 필요합니다.

| 기능 | 메서드 | 엔드포인트 | 설명 |
|------|--------|-----------|------|
| 학적 정보 import | POST | `/api/sync/profile/import` | 학번, 학년, 전공 저장 |
| 시간표 import | POST | `/api/sync/timetable/import` | 확장이 수집한 MML/SSV 시간표 저장 |
| 시간표 sync (레거시) | POST | `/api/sync/timetable` | EDWARD 자격증명 일회성 동기화 |
| 성적 import | POST | `/api/sync/grades/import` | 학기별 성적 SSV 저장 |
| CTL 과제 import | POST | `/api/sync/assignments/import` | 진행 중·미제출 과제 저장 |

---

## 학적 정보 — `POST /api/sync/profile/import`

EDWARD에서 파싱한 학번·학년·전공을 `users` 테이블에 반영합니다.

### Request (예시)

```json
{
  "studentNumber": "20241234",
  "major": "컴퓨터공학과",
  "grade": 2
}
```

### Response (예시)

```json
{
  "studentNumber": "20241234",
  "major": "컴퓨터공학과",
  "grade": 2,
  "user": { "...": "UserResponseDto" }
}
```

---

## 시간표 — `POST /api/sync/timetable/import`

확장이 EDWARD에서 수집한 수강 데이터를 학기·수업·시간표·엔트리로 저장합니다.

### 파싱 전략

1. **수강신청확인 SSV** (`findTlsnGvupAplyList.do`) — 강의 시간
2. **수강신청확인서 MML** — 학점 보강
3. SSV 실패 시 MML 확인서 fallback (`ConfirmationMmlParser`)

### Response (예시)

```json
{
  "semesterId": 1,
  "timetableId": 1,
  "semesterTitle": "2026-1학기",
  "courseCount": 5,
  "entryCount": 8
}
```

저장된 시간표 제목은 `"EDWARD 동기화"`입니다.

---

## 성적 — `POST /api/sync/grades/import`

EDWARD 학기 성적 SSV를 파싱해 `semester_grades` 테이블에 저장합니다.  
마이페이지 `GET /api/users/me/semester-grades`로 조회합니다.

### Response (예시)

```json
{
  "semesterCount": 4,
  "totalCompletedCredits": 72,
  "averageGpa": 3.85,
  "academicWarningCount": 0
}
```

---

## CTL 과제 — `POST /api/sync/assignments/import`

CTL **내 강의실**에서 수강 과목을 조회하고, 과목별 **과제 목록**에서 `[진행중]` + `미제출` 항목만 추출해 `assignments` 테이블에 upsert합니다.

### Response (예시)

```json
{
  "createdCount": 3,
  "updatedCount": 1,
  "skippedCount": 2,
  "fetchedCount": 6,
  "assignments": [ "...AssignmentResponse" ]
}
```

---

## 연관 도메인

| Sync 결과 | 저장 도메인 | README |
|-----------|-------------|--------|
| 학적 정보 | `user` | [user/README.md](../user/README.md) |
| 시간표 | `semester`, `course`, `timetable`, `entry` | [semester](../semester/README.md), [timetable](../timetable/README.md), [course](../course/README.md), [entry](../entry/README.md) |
| 성적 | `semester_grades` | `GET /api/users/me/semester-grades` |
| CTL 과제 | `assignment` | 과제 모듈 (팀원 담당) |

---

## 프론트엔드 · 확장 연동

1. 사용자가 마이페이지 **학사 정보 동기화** 실행
2. `lumos-web` → `chrome.runtime.sendMessage` (확장 ID: `mjbkpdkmolfjmkfaollkpnjfhejnahop`)
3. `lumos-extension`이 portal SSO로 EDWARD/CTL 데이터 수집
4. 확장이 Firebase ID 토큰과 함께 위 import API 호출
5. 프론트엔드가 시간표·과제 세션 캐시 갱신

자세한 확장 설정: 저장소 루트 [`lumos-extension/README.md`](../../../../../../../../lumos-extension/README.md)

---

## 주의사항

- SSO 세션(`portal.kmu.ac.kr`)이 없으면 동기화가 실패합니다.
- EDWARD 학번과 Lumos 가입 계정의 학번이 다르면 `EdwardStudentNumberGuard`가 거부할 수 있습니다.
- import는 멱등성을 고려해 설계되었으나, 동일 학기 재동기화 시 기존 EDWARD 시간표 데이터가 갱신됩니다.
