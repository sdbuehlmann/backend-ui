package ch.donkeycode.backendui;

import ch.donkeycode.backendui.dto.ChildElementValuesDto;
import ch.donkeycode.backendui.dto.ElementValueDto;
import ch.donkeycode.backendui.dto.HtmlElementUpdateDto;
import ch.donkeycode.backendui.form.FormRenderer;
import ch.donkeycode.backendui.form.examples.ExampleForm;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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

        private final AtomicReference<List<FormRenderer.RendererdForm<?>>> renderedForms = new AtomicReference<>(new ArrayList<>());

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

            Thread.sleep(2000);

            val exampleForm = ExampleForm.builder()
                    .prename("Alina")
                    .name("Abplanalp")
                    .build();

            val rendered = new FormRenderer<>(ExampleForm.FORM, exampleForm).render();

            renderedForms.updateAndGet(rendererdForms -> {
                rendererdForms.add(rendered);
                return rendererdForms;
            });

            session.sendMessage(new TextMessage(om.writeValueAsString(HtmlElementUpdateDto.builder()
                    .elementId("loading")
                    .elementHtml(rendered.getHtml())
                    .build())));
        }

        @Override
        public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
            // Hier kannst du die eingehenden Nachrichten verarbeiten
            LOG.info("Message received: {}", message);

            try {
                val childElementValuesDto = om.readValue(
                        message.getPayload().toString(),
                        ChildElementValuesDto.class);

                LOG.info("Deserialized: {}", childElementValuesDto);

                renderedForms.updateAndGet(rendererdForms -> {
                    rendererdForms.stream()
                            .filter(rendererdForm -> rendererdForm.getId().equals(childElementValuesDto.getParentElementId()))
                            .findAny()
                            .ifPresentOrElse(
                                    rendererdForm -> handle(rendererdForm, childElementValuesDto),
                                    () -> LOG.error("Form not found: {}", childElementValuesDto.getParentElementId())
                            );

                    return rendererdForms;
                });
            } catch (Exception ex) {
                LOG.error("Unexpected exception", ex);
            }
        }

        private void handle(final FormRenderer.RendererdForm<?> form, final ChildElementValuesDto childElementValuesDto) {
            // handle values
            childElementValuesDto
                    .getElementValues()
                    .forEach(elementValueDto -> handle(form, elementValueDto));

            // handle actions
            form.getActionBindings().stream()
                    .filter(actionBinding -> actionBinding.getActionId().equals(childElementValuesDto.getTriggeringActionId()))
                    .findAny()
                    .ifPresentOrElse(
                            actionBinding -> actionBinding
                                    .getAction()
                                    .getOnAction()
                                    .run(),
                            () -> LOG.error("Action not found: {}", childElementValuesDto.getParentElementId())
                    );
        }

        private void handle(final FormRenderer.RendererdForm<?> form, final ElementValueDto elementValueDto) {
            form.getBindings().stream()
                    .filter(elementBinding -> elementBinding.getElementId().equals(elementValueDto.getElementId()))
                    .findAny()
                    .ifPresentOrElse(
                            elementBinding -> {
                                elementBinding
                                        .getProperty()
                                        .getValuePersistor()
                                        .persistUnchecked(
                                                form.getData(),
                                                elementValueDto.getValue()
                                        );
                            },
                            () -> LOG.error("Element not found: {}", elementValueDto.getElementId())
                    );
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

