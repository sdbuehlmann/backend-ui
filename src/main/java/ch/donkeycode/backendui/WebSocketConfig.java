package ch.donkeycode.backendui;

import ch.donkeycode.backendui.common.Store;
import ch.donkeycode.backendui.frontend.dto.be2ui.SetInnerHtmlDto;
import ch.donkeycode.backendui.frontend.dto.ui2be.RemovedElementsDto;
import ch.donkeycode.backendui.frontend.dto.ui2be.ResponseDto;
import ch.donkeycode.backendui.navigation.NavigationService;
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

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public static class MyWebSocketHandler implements WebSocketHandler {

        private static final UUID MAIN_ELEMENT_ID = UUID.fromString("0490eee0-c48a-42c6-9296-be94c83acb2d");
        private static final UUID ELEMENTS_REMOVED_RESPONSE_ID = UUID.fromString("65284f2b-8516-4d10-83b9-119444e274d4");

        private final ObjectMapper om = new ObjectMapper();

        private final NavigationService navigationService;

        private final Store<UUID, ResponseHandler<?>> responseHandlersStore = new Store<>(builder -> builder
                .keyExtractor(ResponseHandler::getResponseId)
                .initValues(Set.of(GenericResponseHandler.<RemovedElementsDto>builder()
                        .responseId(ELEMENTS_REMOVED_RESPONSE_ID)
                        .relatedElementId(MAIN_ELEMENT_ID)
                        .handledType(RemovedElementsDto.class)
                        .responseHandler(removedElementsDto -> cleanUpHandlersForElements(removedElementsDto.getIds()))
                        .build())));

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            navigationService
                    .context((displayableElement, containerId) -> sendUpdate(session, displayableElement, containerId), MAIN_ELEMENT_ID)
                    .displayRoot();
        }

        @Override
        public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
            // Hier kannst du die eingehenden Nachrichten verarbeiten
            try {
                val responseDto = om.readValue(
                        message.getPayload().toString(),
                        ResponseDto.class);

                LOG.trace("Message received: {}", responseDto);

                responseHandlersStore.findById(responseDto.getId())
                        .ifPresentOrElse(
                                responseHandler -> handle(responseHandler, responseDto),
                                () -> LOG.error("Response handler not found for response {}", responseDto.getId()));

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
            LOG.info("Connection closed: {}", closeStatus);
        }

        @Override
        public boolean supportsPartialMessages() {
            return false;
        }

        @SneakyThrows
        private void sendUpdate(WebSocketSession session, DisplayableElement element, UUID containerId) {
            responseHandlersStore.addAll(element.getResponseHandlers());

            val payload = SetInnerHtmlDto.builder()
                    .containerId(containerId)
                    .html(element.getHtml())
                    .build();

            synchronized (session) {
                LOG.trace("Send update: {}", payload);
                session.sendMessage(new TextMessage(om.writeValueAsString(payload)));
            }
        }

        private void cleanUpHandlersForElements(Set<UUID> removedElementIds) {
            val removed = responseHandlersStore.selectAndRemove(responseHandler -> removedElementIds
                    .contains(responseHandler.getRelatedElementId()));

            if (LOG.isInfoEnabled() && !removed.isEmpty()) {
                LOG.info("Removed handlers:\n - {}", removed.stream()
                        .map(responseHandler -> String.format(
                                "element#%s; response#%s",
                                responseHandler.getRelatedElementId(),
                                responseHandler.getResponseId()))
                        .collect(Collectors.joining("\n - ")));
            }
        }
    }
}

