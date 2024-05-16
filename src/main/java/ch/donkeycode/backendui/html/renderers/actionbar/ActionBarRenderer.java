package ch.donkeycode.backendui.html.renderers.actionbar;

import ch.donkeycode.backendui.DisplayableElement;
import ch.donkeycode.backendui.ResponseHandler;
import ch.donkeycode.backendui.frontend.functions.Run;
import ch.donkeycode.backendui.html.renderers.model.RenderableRunnable;
import ch.donkeycode.backendui.html.utils.HtmlElement;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class ActionBarRenderer {

    private final List<RenderableRunnable> renderableRunnables;

    private final List<ResponseHandler<?>> responseHandlers = new ArrayList<>();
    private final UUID elementId = UUID.randomUUID();

    public DisplayableElement render() {
        val html = createActionsBar(renderableRunnables);

        return DisplayableElement.builder()
                .html(html.toString())
                .responseHandlers(responseHandlers)
                .build();
    }

    private HtmlElement createActionsBar(final List<RenderableRunnable> runnables) {
        return HtmlElement.builder()
                .name("div")
                .idAttribute(elementId)
                .content(runnables.stream()
                        .map(this::createActionButton))
                .build();
    }

    private HtmlElement createActionButton(final RenderableRunnable runnable) {
        val run = Run.builder()
                .relatedElementId(elementId)
                .runnable(runnable.getRunnable())
                .build();

        responseHandlers.add(run);

        return HtmlElement.builder()
                .name("button")
                .attribute("onclick", run.asJsFunction())
                .content(runnable.getTitle())
                .build();
    }
}
