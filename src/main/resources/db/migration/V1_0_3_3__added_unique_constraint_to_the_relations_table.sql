ALTER TABLE user_robot_relations
    ADD UNIQUE(user_id, robot_id);
