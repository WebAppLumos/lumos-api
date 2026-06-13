ALTER TABLE semesters ADD COLUMN sort_order INT;

UPDATE semesters s
SET sort_order = sub.rn
FROM (
    SELECT semester_id, ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY semester_id) - 1 AS rn
    FROM semesters
) sub
WHERE s.semester_id = sub.semester_id;

ALTER TABLE semesters ALTER COLUMN sort_order SET NOT NULL;
