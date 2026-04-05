package com.nemo.rexus.Sock.Signal.Signals;

import com.fasterxml.jackson.annotation.JsonValue;
import com.nemo.rexus.Decibel.RobotEntity;
import com.nemo.rexus.Decibel.UserEntity;
import com.nemo.rexus.Sock.Signal.SignalContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.function.Supplier;

@Slf4j
enum SignalType {

	OFFER("offer", OfferSignalHandler::new),
	ANSWER("answer", BaseSignalHandler::new),
	ICE_CANDIDATE("iceCandidate", IceCandidateHandler::new),
	TEXT("text", null),
	ERROR("error", null);

	SignalType(@NotNull String value, @Nullable Supplier<@NotNull SignalHandler> signalHandler) {
		this.value = value;
		this.signalHandler = signalHandler == null ? null : signalHandler.get();
	}

	@Getter
	@JsonValue
	private final @NotNull String value;
	private final @Nullable SignalHandler signalHandler;

	public void handle(@NotNull SignalMessage message, @NotNull SignalContext context) throws IOException {
		if (signalHandler == null) {
			return;
		}

		Object principal = context.principal();
		switch (principal) {
			case RobotEntity robotPrincipal -> signalHandler.handleForRobot(message, robotPrincipal, context);
			case UserEntity userPrincipal -> signalHandler.handleForOperator(message, userPrincipal, context);
			case null -> throw new IllegalArgumentException("Not authorized");
			default -> log.warn("Unknown principal type when handling message: {}", principal.getClass().getName());
		}
	}

}
