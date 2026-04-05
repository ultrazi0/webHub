package com.nemo.rexus.Sock.Signal.Signals;

import com.nemo.rexus.Decibel.UserEntity;
import com.nemo.rexus.Sock.Signal.SignalContext;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

class IceCandidateHandler extends BaseSignalHandler {

	@Override
	public void handleForOperator(@NotNull SignalMessage message, @NotNull UserEntity user, @NotNull SignalContext context) throws IOException {
		verifyOperatorMessage(message, user, context);
		Integer robotId = message.getRobotId();

		if (context.robotSessions().containsKey(robotId)) {
			context.robotSessions().get(robotId).sendMessage(message.toTextMessage());
		}
	}
}
