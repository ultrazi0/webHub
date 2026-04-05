package com.nemo.rexus.Sock.Signal.Signals;

import com.nemo.rexus.Decibel.RobotEntity;
import com.nemo.rexus.Decibel.RobotNotFoundException;
import com.nemo.rexus.Decibel.UserEntity;
import com.nemo.rexus.Sock.Signal.SignalContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

import java.io.IOException;

class BaseSignalHandler implements SignalHandler {

	@Override
	@SuppressWarnings("resource")
	public void handleForOperator(@NotNull SignalMessage message, @NotNull UserEntity user, @NotNull SignalContext context) throws IOException {
		verifyOperatorMessage(message, user, context);
		Integer robotId = message.getRobotId();

		if (context.robotSessions().containsKey(robotId)) {
			context.robotSessions().get(robotId).sendMessage(message.toTextMessage());
		} else {
			context.session().sendMessage(
					new SignalMessage(SignalType.ERROR, "The robot is not connected").toTextMessage()
			);
		}

	}

	/// This method **mutates** {@code message}!
	protected static void verifyOperatorMessage(@NotNull SignalMessage message, @NotNull UserEntity user, @NotNull SignalContext context) {
		Integer robotId = message.getRobotId();
		if (robotId == null) {
			throw new IllegalArgumentException("Robot ID is not provided");
		}

		int allowedRobotId = context.robotRepository().findRobotIdByRobotIdAndUserIdIfAllowed(robotId, user.getId());
		Assert.isTrue(robotId.equals(allowedRobotId), "IDs must match");

		message.setOperatorId(user.getId());
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

		message.setRobotId(robot.getId());

		context.operatorSessions().get(operatorId).sendMessage(message.toTextMessage());
	}
}
