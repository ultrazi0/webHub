ALTER TABLE robots
    ADD COLUMN password UUID DEFAULT gen_random_uuid(),
    DROP CONSTRAINT robots_name_key,
    ADD UNIQUE (owner_id, name);

UPDATE robots
    SET password = gen_random_uuid();

ALTER TABLE robots
    ALTER COLUMN password SET NOT NULL;
