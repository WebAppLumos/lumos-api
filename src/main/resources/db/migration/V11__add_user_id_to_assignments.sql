ALTER TABLE assignments 
ADD COLUMN user_id VARCHAR(255) NOT NULL DEFAULT 'unknown_user';

ALTER TABLE assignments 
ADD CONSTRAINT uk_assignments_user_course_title UNIQUE (user_id, course, title);