CREATE OR REPLACE FUNCTION add_relation_when_new_robot_inserted()
    RETURNS TRIGGER AS
$$
BEGIN
    INSERT INTO bot.user_robot_relations (user_id, robot_id) VALUES (NEW.owner_id, NEW.robot_id);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER after_insert_robot_trigger
    AFTER INSERT ON bot.robots
    FOR EACH ROW
    EXECUTE FUNCTION add_relation_when_new_robot_inserted();
