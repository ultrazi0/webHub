package com.nemo.webHub.Sock;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.nemo.webHub.Decibel.RobotRepository;
import com.nemo.webHub.Robot.RobotService;
import com.nemo.webHub.Sect.HandshakeInterceptors.CommandClientHandshakeInterceptor;
import com.nemo.webHub.Sect.HandshakeInterceptors.CommandRobotHandshakeInterceptor;
import com.nemo.webHub.Sect.HandshakeInterceptors.ImageClientHandshakeInterceptor;
import com.nemo.webHub.Sect.HandshakeInterceptors.ImageRobotHandshakeInterceptor;
import com.nemo.webHub.Sock.Command.CommandClientHandler;
import com.nemo.webHub.Sock.Command.CommandRobotHandler;
import com.nemo.webHub.Sock.Image.ImageRobotHandler;
import com.nemo.webHub.Sock.Image.ImageClientHandler;
import com.nemo.webHub.Sock.Image.ImageSubscribers;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSocket
public class WebSockConfig implements WebSocketConfigurer {

    @Autowired
    private Operators operators;
    @Autowired
    private RobotService robotService;
    @Autowired
    private RobotRepository robotRepository;
    @Autowired
    private ImageSubscribers imageSubscribers;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(imageClientHandler(), "/api/image/client/{robotId}")
                .addInterceptors(new ImageClientHandshakeInterceptor(robotRepository)).setAllowedOrigins("*");

        registry.addHandler(imageRobotHandler(), "/api/image/robot")
                .addInterceptors(new ImageRobotHandshakeInterceptor(imageSubscribers));

        registry.addHandler(commandRobotHandler(), "/api/command/robot")
                .addInterceptors(new CommandRobotHandshakeInterceptor(robotService));

        registry.addHandler(commandClientHandler(), "/api/command/client/{robotId}")
                .addInterceptors(new CommandClientHandshakeInterceptor(robotRepository, operators)).setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler imageClientHandler() {
        return new ImageClientHandler();
    }

    @Bean
    public WebSocketHandler imageRobotHandler() {
        return new ImageRobotHandler();
    }

    @Bean
    public WebSocketHandler commandRobotHandler() {
        return new CommandRobotHandler();
    }

    @Bean
    public WebSocketHandler commandClientHandler() {
        return new CommandClientHandler();
    }

    @Bean
    public ServletServerContainerFactoryBean createWebsocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize((int) Math.pow(2, 20));

        return container;
    }

    public static TextMessage createRegularJsonTextMessage(@NotNull CharSequence message) throws IOException {
        // TODO: rewrite this functionality as a separate class with different message types
        // See https://www.baeldung.com/jackson-streaming-api

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator jsonGenerator = jsonFactory.createGenerator(stream, JsonEncoding.UTF8);

        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("messageType", "regularMessage");
        jsonGenerator.writeStringField("message", message.toString());
        jsonGenerator.writeEndObject();

        jsonGenerator.close();

        return new TextMessage(stream.toString(StandardCharsets.UTF_8));
    }

    @Deprecated
    private static Map<String, String> getQueryParametersMap(String query) {
        Map<String, String> map = new HashMap<>();

        for (String param : query.split("&")) {
            String[] splitParam = param.split("=");

            map.put(splitParam[0], splitParam[1]);
        }

        return map;
    }
}
