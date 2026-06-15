ALTER TABLE users
    ALTER COLUMN income_bracket TYPE VARCHAR(50)
    USING income_bracket::text;
