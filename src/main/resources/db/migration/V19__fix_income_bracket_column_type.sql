-- Some environments created income_bracket as VARCHAR; align with JPA INTEGER mapping.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'users'
          AND column_name = 'income_bracket'
          AND data_type IN ('character varying', 'text')
    ) THEN
        ALTER TABLE users
            ALTER COLUMN income_bracket TYPE INTEGER
            USING (
                CASE
                    WHEN income_bracket IS NULL OR BTRIM(income_bracket::text) = '' THEN NULL
                    WHEN BTRIM(income_bracket::text) ~ '^[0-9]+$' THEN BTRIM(income_bracket::text)::INTEGER
                    ELSE NULLIF(REGEXP_REPLACE(income_bracket::text, '[^0-9]', '', 'g'), '')::INTEGER
                END
            );
    END IF;
END $$;
