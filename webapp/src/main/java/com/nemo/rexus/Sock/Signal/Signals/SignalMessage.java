package com.nemo.rexus.Sock.Signal.Signals;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nemo.rexus.Sock.Signal.SignalContext;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.socket.TextMessage;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.StringNode;

import java.io.IOException;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter(AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignalMessage {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private @NotNull SignalType type;
	private @NotNull JsonNode payload;

	private @Nullable Integer robotId;
	private @Nullable Integer operatorId;

	SignalMessage(@NotNull SignalType type, @NotNull String payload) {
		this.type = type;
		this.payload = new StringNode(payload);
	}

	public void handle(@NotNull SignalContext context) throws IOException {
		type.handle(this, context);
	}

	public @NotNull TextMessage toTextMessage() {
		return new TextMessage(OBJECT_MAPPER.writeValueAsString(this));
	}

	public static @NotNull SignalMessage disconnected() {
		return new SignalMessage(
				SignalType.DISCONNECT,
				OBJECT_MAPPER.valueToTree(Map.of(
						"timestamp", System.currentTimeMillis()
				)),
				null,
				null
		);
	}

}
