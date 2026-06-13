ALTER TABLE timetables ADD COLUMN sort_order INT;

UPDATE timetables t
SET sort_order = sub.rn
FROM (
    SELECT timetable_id, ROW_NUMBER() OVER (PARTITION BY semester_id ORDER BY timetable_id) - 1 AS rn
    FROM timetables
) sub
WHERE t.timetable_id = sub.timetable_id;

ALTER TABLE timetables ALTER COLUMN sort_order SET NOT NULL;
