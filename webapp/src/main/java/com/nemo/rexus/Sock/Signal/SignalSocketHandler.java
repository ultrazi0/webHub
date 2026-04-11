package com.nemo.rexus.Sock.Signal;

import static com.nemo.rexus.Sock.Signal.SignalContext.extractPrincipal;

import com.nemo.rexus.Decibel.RobotEntity;
import com.nemo.rexus.Decibel.RobotRepository;
import com.nemo.rexus.Decibel.UserEntity;
import com.nemo.rexus.Sock.Signal.Signals.SignalMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
public class SignalSocketHandler extends TextWebSocketHandler {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private final RobotRepository robotRepository;

	private final @NotNull Map<Integer, @NotNull WebSocketSession> robotSessions = new ConcurrentHashMap<>();
	private final @NotNull Map<Integer, @NotNull WebSocketSession> operatorSessions = new ConcurrentHashMap<>();

	@Override
	public void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) throws IOException {
		log.trace("Received message: {} from {}", message, session);

		SignalMessage signalMessage = OBJECT_MAPPER.readValue(message.getPayload(), SignalMessage.class);

		signalMessage.handle(new SignalContext(
				session,
				robotSessions,
				operatorSessions,
				robotRepository
		));
	}

	@Override
	public void afterConnectionEstablished(@NotNull WebSocketSession session) {
		Object principal = extractPrincipal(session);
		switch (principal) {
			case RobotEntity robotPrincipal -> robotSessions.put(robotPrincipal.getId(), session);
			case UserEntity userPrincipal -> operatorSessions.put(userPrincipal.getId(), session);
			case null -> log.warn("No principal found when establishing connection");
			default -> log.warn("Unknown principal type when establishing connection: {}", principal.getClass().getName());
		}
	}

	@Override
	public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) throws IOException {
		Object principal = extractPrincipal(session);
		switch (principal) {
			case RobotEntity robotPrincipal -> {
				WebSocketSession operatorSession = operatorSessions.get(robotPrincipal.getId());
				if (operatorSession != null) {
					operatorSession.sendMessage(
							SignalMessage.disconnected().toTextMessage()
					);
				}
				robotSessions.remove(robotPrincipal.getId(), session);
			}
			case UserEntity userPrincipal -> {
				WebSocketSession robotSession = robotSessions.get(userPrincipal.getId());
				if (robotSession != null) {
					robotSession.sendMessage(
							SignalMessage.disconnected().toTextMessage()
					);
				}
				operatorSessions.remove(userPrincipal.getId(), session);
			}
			case null -> log.warn("No principal found when closing connection");
			default -> log.warn("Unknown principal type when closing connection: {}", principal.getClass().getName());
		}
	}
}
