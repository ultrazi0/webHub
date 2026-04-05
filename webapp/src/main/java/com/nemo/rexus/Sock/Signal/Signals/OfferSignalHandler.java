package com.nemo.rexus.Sock.Signal.Signals;

import com.nemo.rexus.Decibel.RobotEntity;
import com.nemo.rexus.Sock.Signal.SignalContext;
import org.jetbrains.annotations.NotNull;

class OfferSignalHandler extends BaseSignalHandler {

	@Override
	public void handleForRobot(@NotNull SignalMessage message, @NotNull RobotEntity robot, @NotNull SignalContext context) {
		throw new IllegalCallerException("Robots cannot initiate connections");
	}
}
