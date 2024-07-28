ALTER TABLE robots
ADD CHECK (TRIM(name) != '');
