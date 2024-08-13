CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(25) UNIQUE NOT NULL,
    password TEXT NOT NULL CHECK (LENGTH(TRIM(password)) > 0),
    roles VARCHAR(15)[] NOT NULL,
    created_at TIMESTAMPTZ NOT NUll DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE robots
    RENAME COLUMN id TO robot_id;
ALTER TABLE robots
    ALTER COLUMN created_at TYPE TIMESTAMPTZ,
    ADD COLUMN owner_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS user_robot_relations (
    relation_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    robot_id INT NOT NULL REFERENCES robots(robot_id) ON DELETE CASCADE,
    UNIQUE (user_id, robot_id)
);
