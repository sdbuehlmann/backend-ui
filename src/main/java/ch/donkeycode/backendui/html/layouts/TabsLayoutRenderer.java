package ch.donkeycode.backendui.html.layouts;

import ch.donkeycode.backendui.ResponseHandler;
import ch.donkeycode.backendui.frontend.functions.Run;
import ch.donkeycode.backendui.html.colors.ColorScheme;
import ch.donkeycode.backendui.html.renderers.model.RenderableRunnable;
import ch.donkeycode.backendui.html.utils.CssStyle;
import ch.donkeycode.backendui.html.utils.HtmlElement;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class TabsLayoutRenderer {

    private final List<RenderableRunnable> renderableRunnables;
    private final ColorScheme colorScheme;

    private final List<ResponseHandler<?>> responseHandlers = new ArrayList<>();
    private final UUID elementId = UUID.randomUUID();
    private final UUID containerId = UUID.randomUUID();

    public Result render() {
        val html = HtmlElement.builder()
                .div()
                .idAttribute(elementId)
                .styleAttribute(new CssStyle()
                        .add("width", "100%")
                        .add("height", "100%")
                        .add("display", "grid")
                        .color(colorScheme.getText())
                        .add("grid-template-columns", "minmax(150px, 25%) 1fr"))
                .content(
                        // tabs container
                        HtmlElement.builder()
                                .div()
                                .styleAttribute(new CssStyle()
                                        .backgroundColor(colorScheme.getDarker()))
                                .content(renderableRunnables.stream()
                                        .map(this::createTab))
                                .build(),
                        // views container
                        HtmlElement.builder()
                                .div()
                                .styleAttribute(new CssStyle()
                                        .add("padding", "10px")
                                        .backgroundColor(colorScheme.getPrimary()))
                                .content(HtmlElement.builder()
                                        .div()
                                        .idAttribute(containerId)
                                        .build())
                                .build()
                )
                .build();

        return Result.builder()
                .containerId(containerId)
                .html(html)
                .responseHandlers(responseHandlers)
                .build();
    }

    private HtmlElement createTab(RenderableRunnable renderableRunnable) {
        val style = new CssStyle()
                .add("margin", "10px")
                .add("padding", "10px")
                .backgroundColor(colorScheme.getDarker())
                .add("cursor", "pointer");

        val run = Run.builder()
                .relatedElementId(elementId)
                .runnable(renderableRunnable.getRunnable())
                .build();

        val html = HtmlElement.builder()
                .name("div")
                .styleAttribute(style)
                .attribute("onClick", run.asJsFunction())
                .content(renderableRunnable.getTitle())
                .build();

        responseHandlers.add(run);

        return html;
    }

    @Value
    @Builder
    public static class Result {
        @NonNull
        UUID containerId;

        @NonNull
        HtmlElement html;

        @NonNull
        List<ResponseHandler<?>> responseHandlers;
    }
}
