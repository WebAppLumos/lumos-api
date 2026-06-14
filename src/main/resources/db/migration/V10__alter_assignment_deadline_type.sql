ALTER TABLE assignments 
ALTER COLUMN deadline TYPE date 
USING deadline::date;