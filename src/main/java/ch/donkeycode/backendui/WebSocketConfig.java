package ch.donkeycode.backendui;

import ch.donkeycode.backendui.dto.HtmlElementUpdateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@Slf4j
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new MyWebSocketHandler(), "/websocket")
                .setAllowedOrigins("*"); // Hier kannst du auch die erlaubten Ursprünge (Origins) konfigurieren
    }

    public static class MyWebSocketHandler implements WebSocketHandler {

        private final ObjectMapper om = new ObjectMapper();

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//            session.sendMessage(new TextMessage("Hello Client"));
//            Thread.sleep(2000);
//            session.sendMessage(new TextMessage("Hello Client!!!!"));

            Thread.sleep(2000);

            val update = HtmlElementUpdateDto.builder()
                    .elementId("loading")
                    .elementHtml("<h1 style=\"font-family: 'Agency FB'; color: white\" id=\"loading\">\n" +
                            "        Backend UI ready!\n" +
                            "    </h1>")
                    .build();

            val json = om.writeValueAsString(update);
            LOG.info("Send json: {}", json);

            session.sendMessage(new TextMessage(json));
        }

        @Override
        public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
            // Hier kannst du die eingehenden Nachrichten verarbeiten
        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
            // Hier kannst du Transportfehler behandeln
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
            // Hier kannst du auf Verbindungsabbau reagieren
        }

        @Override
        public boolean supportsPartialMessages() {
            return false;
        }
    }
}

