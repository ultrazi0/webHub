package com.nemo.rexus.Sock.Signal;

import com.nemo.rexus.Decibel.RobotRepository;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.WebSocketSession;

import java.security.Principal;
import java.util.Map;

@Slf4j
public record SignalContext(
		@NotNull WebSocketSession session,
		@NotNull Map<Integer, @NotNull WebSocketSession> robotSessions,
		@NotNull Map<Integer, @NotNull WebSocketSession> operatorSessions,
		@NotNull RobotRepository robotRepository
) {

	public @Nullable Object principal() {
		return extractPrincipal(session);
	}

	static @Nullable Object extractPrincipal(@NotNull WebSocketSession session) {
		Principal principal = session.getPrincipal();
		if (principal instanceof Authentication authentication) {
			return authentication.getPrincipal();
		}

		log.warn("Principal is not an instance of Authentication");
		return null;
	}

}
