package com.nemo.rexus.Sock.Signal.Signals;

import com.nemo.rexus.Decibel.RobotEntity;
import com.nemo.rexus.Decibel.UserEntity;
import com.nemo.rexus.Sock.Signal.SignalContext;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

interface SignalHandler {

	void handleForOperator(
			@NotNull SignalMessage message,
			@NotNull UserEntity user,
			@NotNull SignalContext context
	) throws IOException;

	void handleForRobot(
			@NotNull SignalMessage message,
			@NotNull RobotEntity robot,
			@NotNull SignalContext context
	) throws IOException;

}
