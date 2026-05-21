# Student Module

이 모듈은 시스템의 핵심인 학생 정보를 관리하는 기본 엔티티를 포함하고 있습니다.
`Certifications` 및 `Language_Exams` 모듈에서 학생 정보를 참조하기 위한 기반 모듈입니다.

## 주요 엔티티
### Students
- **studentID**: 학생 고유 번호 (PK)
- **name**: 학생 이름

## 사용 예시
다른 모듈에서 학생 정보를 참조할 때 `StudentsRepository`를 주입받아 사용합니다.
현재는 최소한의 정보만 포함하고 있으며, 필요에 따라 학과, 학년 등의 정보를 확장할 수 있습니다.
