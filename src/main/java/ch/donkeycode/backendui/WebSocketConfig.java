package ch.donkeycode.backendui;

import ch.donkeycode.backendui.frontend.ResponseHandler;
import ch.donkeycode.backendui.frontend.dto.HtmlElementUpdateDto;
import ch.donkeycode.backendui.frontend.dto.ResponseDto;
import ch.donkeycode.backendui.html.renderers.model.DisplayableElement;
import ch.donkeycode.backendui.navigation.NavigationService;
import ch.donkeycode.backendui.navigation.NavigationTarget;
import ch.donkeycode.backendui.navigation.Navigator;
import ch.donkeycode.examples.persons.NavigationTargetRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Configuration
@Slf4j
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final NavigationService navigationService;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new MyWebSocketHandler(navigationService), "/websocket")
                .setAllowedOrigins("*"); // Hier kannst du auch die erlaubten Ursprünge (Origins) konfigurieren
    }

    @RequiredArgsConstructor
    public static class MyWebSocketHandler implements WebSocketHandler, Navigator {

        private static final UUID MAIN_ELEMENT_ID = UUID.fromString("0490eee0-c48a-42c6-9296-be94c83acb2d");

        private final ObjectMapper om = new ObjectMapper();

        private final NavigationService navigationService;

        private final AtomicReference<List<DisplayableElement>> currentElements = new AtomicReference<>(new ArrayList<>());

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            Thread.sleep(1000);

            val update = HtmlElementUpdateDto.builder()
                    .elementId(MAIN_ELEMENT_ID)
                    .elementHtml("""
                            <h1 style="background: white;">
                                Backend UI ready!
                            </h1>
                            """)
                    .build();

            val json = om.writeValueAsString(update);
            LOG.info("Send json: {}", json);
            session.sendMessage(new TextMessage(json));
            Thread.sleep(1000);

            navigationService
                    .context((displayableElement, containerId) -> sendUpdate(session, displayableElement, containerId), MAIN_ELEMENT_ID)
                    .display(NavigationTargetRegistry.MAIN, null);
        }

        @Override
        public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
            // Hier kannst du die eingehenden Nachrichten verarbeiten


            try {
                val responseDto = om.readValue(
                        message.getPayload().toString(),
                        ResponseDto.class);

                LOG.info("Message received: {}", responseDto);

                currentElements.updateAndGet(displayedElements -> {
                    displayedElements.stream()
                            .flatMap(displayedElement -> displayedElement.getResponseHandlers().stream())
                            .filter(responseHandler -> responseHandler.getResponseId().equals(responseDto.getId()))
                            .findAny()
                            .ifPresentOrElse(
                                    responseHandler -> handle(responseHandler, responseDto),
                                    () -> LOG.error("Displayed element {} not found", responseDto.getId())
                            );

                    return displayedElements;
                });
            } catch (Exception ex) {
                LOG.error("Unexpected exception", ex);
            }
        }

        private void handle(
                final ResponseHandler<?> responseHandler,
                final ResponseDto responseDto
        ) {
            if (responseDto.getData() != null) {
                val payload = om.convertValue(responseDto.getData(), responseHandler.getHandledType());
                responseHandler.handleResponseUnchecked(payload);
            } else {
                responseHandler.handleResponseUnchecked(null);
            }
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

        @Override
        public <T> void navigate(NavigationTarget<T> target, T data) {

        }

        @SneakyThrows
        private void sendUpdate(WebSocketSession session, DisplayableElement element, UUID containerId) {
            currentElements.updateAndGet(elements -> {
                elements.add(element);
                return elements;
            });

            val payload = HtmlElementUpdateDto.builder()
                    .elementId(containerId)
                    .elementHtml(element.getHtml())
                    .build();

            synchronized (session) {
                LOG.info("Send update: {}", payload);
                session.sendMessage(new TextMessage(om.writeValueAsString(payload)));
            }
        }
    }
}

