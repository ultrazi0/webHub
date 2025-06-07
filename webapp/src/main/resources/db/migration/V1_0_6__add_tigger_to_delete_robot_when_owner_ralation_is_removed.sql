CREATE OR REPLACE FUNCTION remove_robot_when_owner_relation_is_deleted()
    RETURNS TRIGGER AS
$$
BEGIN
    DELETE FROM bot.robots robot WHERE robot.robot_id = OLD.robot_id AND robot.owner_id = OLD.user_id;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE TRIGGER after_owner_relation_delete_trigger
    AFTER DELETE
    ON bot.user_robot_relations
    FOR EACH ROW
    EXECUTE FUNCTION remove_robot_when_owner_relation_is_deleted();
