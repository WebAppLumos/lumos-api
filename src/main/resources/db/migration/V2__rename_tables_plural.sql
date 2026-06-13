-- JPA @Table(name) 복수형과 DB 테이블명을 맞춘다.
-- 이미 V1이 적용된 DB에서는 ALTER RENAME으로 안전하게 전환한다.

ALTER TABLE semester RENAME TO semesters;
ALTER TABLE timetable RENAME TO timetables;
ALTER TABLE entry RENAME TO entries;
ALTER TABLE note RENAME TO notes;

ALTER TABLE semesters RENAME CONSTRAINT pk_semester TO pk_semesters;
ALTER TABLE semesters RENAME CONSTRAINT fk_semester_user TO fk_semesters_user;

ALTER TABLE timetables RENAME CONSTRAINT pk_timetable TO pk_timetables;
ALTER TABLE timetables RENAME CONSTRAINT fk_timetable_semester TO fk_timetables_semester;

ALTER TABLE entries RENAME CONSTRAINT pk_entry TO pk_entries;
ALTER TABLE entries RENAME CONSTRAINT fk_entry_timetable TO fk_entries_timetable;
ALTER TABLE entries RENAME CONSTRAINT fk_entry_course TO fk_entries_course;

ALTER TABLE notes RENAME CONSTRAINT pk_note TO pk_notes;
ALTER TABLE notes RENAME CONSTRAINT fk_note_course TO fk_notes_course;
