package com.nemo.rexus.Sock.Signal.Signals;

import com.nemo.rexus.Decibel.RobotEntity;
import com.nemo.rexus.Decibel.RobotNotFoundException;
import com.nemo.rexus.Decibel.UserEntity;
import com.nemo.rexus.Sock.Signal.SignalContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;
import org.springframework.web.socket.TextMessage;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

class BaseSignalHandler implements SignalHandler {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Override
	@SuppressWarnings("resource")
	public void handleForOperator(@NotNull SignalMessage message, @NotNull UserEntity user, @NotNull SignalContext context) throws IOException {
		Integer robotId = message.getRobotId();
		if (robotId == null) {
			throw new IllegalArgumentException("Robot ID is not provided");
		}

		int allowedRobotId = context.robotRepository().findRobotIdByRobotIdAndUserIdIfAllowed(robotId, user.getId());
		Assert.isTrue(robotId.equals(allowedRobotId), "IDs must match");

		if (context.robotSessions().containsKey(robotId)) {
			context.robotSessions().get(robotId).sendMessage(context.originalMessage());
		} else {
			context.session().sendMessage(new TextMessage(OBJECT_MAPPER.writeValueAsString(
					new SignalMessage(SignalType.ERROR, "The robot is not connected")
			)));
		}

	}

	@Override
	public void handleForRobot(@NotNull SignalMessage message, @NotNull RobotEntity robot, @NotNull SignalContext context) throws IOException {
		Integer operatorId = message.getOperatorId();
		if (operatorId == null) {
			throw new IllegalArgumentException("Operator ID is not provided");
		}

		try {
			context.robotRepository().findRobotIdByRobotIdAndUserIdIfAllowed(robot.getId(), operatorId);
		} catch (RobotNotFoundException e) {
			throw new IllegalArgumentException("Operator is not connected to this robot");
		}

		context.operatorSessions().get(operatorId).sendMessage(context.originalMessage());
	}
}
