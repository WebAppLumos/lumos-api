-- V12 적용 후에도 assignments.deadline 컬럼이 VARCHAR로 남아 있어 Hibernate 검증이 실패함.
-- (flyway repair 등으로 마이그레이션만 기록되고 실제 ALTER가 실행되지 않은 경우)
-- Assignment 엔티티의 LocalDateTime(deadline)과 맞추기 위해 TIMESTAMP(6)로 변환한다.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'assignments'
          AND column_name = 'deadline'
          AND data_type IN ('character varying', 'date')
    ) THEN
        ALTER TABLE assignments
            ALTER COLUMN deadline TYPE TIMESTAMP(6) USING deadline::timestamp;
    END IF;
END $$;
