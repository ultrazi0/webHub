CREATE TABLE IF NOT EXISTS custom_commands
(
    robot_id     INT           NOT NULL REFERENCES robots (robot_id) ON DELETE CASCADE,
    command_type VARCHAR(15)   NOT NULL CHECK ( LENGTH(TRIM(command_type)) > 0 ),
    command_keys VARCHAR(15)[] NOT NULL DEFAULT ARRAY[]::VARCHAR[],

    PRIMARY KEY (robot_id, command_type),
    CONSTRAINT command_type_is_not_a_standard_command
        CHECK (command_type NOT IN ('MOVE', 'TURRET', 'TURRET_CONTINUOUS', 'AIM', 'SHOOT', 'STOP'))
);