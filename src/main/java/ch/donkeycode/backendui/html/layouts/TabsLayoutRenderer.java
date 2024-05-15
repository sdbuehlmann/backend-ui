package ch.donkeycode.backendui.html.layouts;

import ch.donkeycode.backendui.frontend.ResponseHandler;
import ch.donkeycode.backendui.frontend.functions.Run;
import ch.donkeycode.backendui.html.elements.model.DisplayableElement;
import ch.donkeycode.backendui.html.elements.model.RenderableRunnable;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TabsLayoutRenderer {

    private final List<RenderableRunnable> renderableRunnables;

    private final List<ResponseHandler<?>> responseHandlers = new ArrayList<>();
    private final UUID elementId = UUID.randomUUID();
    private final UUID containerId = UUID.randomUUID();

    public Container render() {
        val html = String.format("""
                        <div id="%s" style="width: 100%%; height: 100%%; display: grid; grid-template-columns: minmax(150px, 25%%) 1fr;">
                            <div style="background: yellow">
                                <!-- tabs container -->
                                %s
                            </div>
                            <div style="background: blue">
                                <!-- views container -->
                                <div id="%s">
                                </div>
                            </div>
                        </div>
                        """,
                elementId,
                renderableRunnables.stream()
                        .map(this::createTab)
                        .collect(Collectors.joining()),
                containerId);

        return Container.builder()
                .containerId(containerId)
                .displayableElement(DisplayableElement.builder()
                        .id(elementId)
                        .html(html)
                        .responseHandlers(responseHandlers)
                        .build())
                .build();
    }

    private String createTab(RenderableRunnable renderableRunnable) {
        val style = new CssStyle()
                .add("margin", "10px")
                .add("padding", "10px")
                .add("background-color", "rgba(0, 0, 0, 0.1)")
                .add("cursor", "pointer");

        val run = Run.builder()
                .runnable(renderableRunnable.getRunnable())
                .build();

        val html = HtmlElement.builder()
                .name("div")
                .attribute("style", style.toInlineStyle())
                .attribute("onClick", run.asJsFunction())
                .content(renderableRunnable.getTitle())
                .build();

        responseHandlers.add(run);

        return html.toString();
    }

    private String createViewContainer(RenderableRunnable renderableRunnable) {
        val html = """
                <div>
                    PLACEHOLDER
                </div>
                """;

        return html;
    }

    @Value
    @Builder
    public static class HtmlElement {
        @NonNull
        String name;

        @NonNull
        String content;

        @NonNull
        Set<KeyValue> attributes;

        @Override
        public String toString() {
            return String.format("""
                    <%s %s>
                        %s
                    </%s>
                    """,
                    name,
                    attributes.stream()
                            .map(keyValue -> String.format("%s=\"%s\"", keyValue.getKey(), keyValue.getValue()))
                            .collect(Collectors.joining(" ")),
                    content,
                    name
                    );
        }

        public static class HtmlElementBuilder {
            public HtmlElementBuilder attribute(String key, String value) {
                if (this.attributes == null) {
                    this.attributes = new HashSet<>();
                }
                this.attributes.add(new KeyValue(key, value));
                return this;
            }
        }
    }

    public static class CssStyle {
        private final List<KeyValue> entries = new ArrayList<>();

        public CssStyle add(String key, String value) {
            entries.add(new KeyValue(key, value));
            return this;
        }

        public String toInlineStyle() {
            return entries.stream()
                    .map(cssStyleEntry -> String.format("%s: %s;", cssStyleEntry.getKey(), cssStyleEntry.getValue()))
                    .collect(Collectors.joining());

        }
    }

    @Value
    public static class KeyValue {
        @NonNull
        String key;
        @NonNull
        String value;
    }

    @Value
    @Builder
    public static class Container {
        @NonNull
        UUID containerId;

        @NonNull
        DisplayableElement displayableElement;
    }
}
