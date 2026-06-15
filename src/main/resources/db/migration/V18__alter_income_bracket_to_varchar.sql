-- users 테이블의 income_bracket(소득분위) 컬럼 타입을 INTEGER에서 VARCHAR(50)으로 변경
ALTER TABLE users ALTER COLUMN income_bracket TYPE VARCHAR(50);
