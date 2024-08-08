CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(25) UNIQUE NOT NULL,
    roles VARCHAR(15)[] NOT NULL,
    created_at TIMESTAMPTZ NOT NUll DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE robots
    RENAME COLUMN id TO robot_id;
ALTER TABLE robots
    ALTER COLUMN created_at TYPE TIMESTAMPTZ;

CREATE TABLE IF NOT EXISTS user_robot_relations (
    relation_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id) ON DELETE CASCADE,
    user_is_owner BOOLEAN NOT NULL DEFAULT FALSE,
    robot_id INT REFERENCES robots(robot_id) ON DELETE CASCADE
);

CREATE OR REPLACE FUNCTION delete_robot_if_owner_relation_deleted()
RETURNS TRIGGER AS
$$
BEGIN
    IF OLD.user_is_owner = TRUE THEN
        DELETE FROM robots WHERE robot_id = OLD.robot_id;
    END IF;
    RETURN OLD;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER delete_owned_robot_on_relation_delete
AFTER DELETE ON user_robot_relations
FOR EACH ROW
EXECUTE FUNCTION delete_robot_if_owner_relation_deleted();
