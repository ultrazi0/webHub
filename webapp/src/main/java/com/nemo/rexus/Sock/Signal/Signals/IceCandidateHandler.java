package com.nemo.rexus.Sock.Signal.Signals;

import com.nemo.rexus.Decibel.UserEntity;
import com.nemo.rexus.Sock.Signal.SignalContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

import java.io.IOException;

class IceCandidateHandler extends BaseSignalHandler {

	@Override
	public void handleForOperator(@NotNull SignalMessage message, @NotNull UserEntity user, @NotNull SignalContext context) throws IOException {
		Integer robotId = message.getRobotId();
		if (robotId == null) {
			throw new IllegalArgumentException("Robot ID is not provided");
		}

		int allowedRobotId = context.robotRepository().findRobotIdByRobotIdAndUserIdIfAllowed(robotId, user.getId());
		Assert.isTrue(robotId.equals(allowedRobotId), "IDs must match");

		if (context.robotSessions().containsKey(robotId)) {
			context.robotSessions().get(robotId).sendMessage(context.originalMessage());
		}
	}
}
