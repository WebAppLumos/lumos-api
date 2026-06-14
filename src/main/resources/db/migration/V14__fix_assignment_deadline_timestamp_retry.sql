-- V13이 flyway repair로 기록만 남고 실제 ALTER가 실행되지 않은 경우를 처리한다.
-- pg_catalog로 컬럼 타입을 확인한 뒤 TIMESTAMP(6)가 아니면 변환한다.
DO $$
DECLARE
    col_type text;
BEGIN
    SELECT format_type(a.atttypid, a.atttypmod)
    INTO col_type
    FROM pg_attribute a
    JOIN pg_class c ON c.oid = a.attrelid
    JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE n.nspname = 'public'
      AND c.relname = 'assignments'
      AND a.attname = 'deadline'
      AND NOT a.attisdropped;

    IF col_type IS NOT NULL AND col_type NOT LIKE 'timestamp%' THEN
        ALTER TABLE assignments
            ALTER COLUMN deadline TYPE TIMESTAMP(6) USING deadline::timestamp;
    END IF;
END $$;
