package ch.donkeycode.backendui.html.elements.actionbar;

import ch.donkeycode.backendui.frontend.ResponseHandler;
import ch.donkeycode.backendui.frontend.functions.Run;
import ch.donkeycode.backendui.html.elements.model.DisplayableElement;
import ch.donkeycode.backendui.html.elements.model.RenderableRunnable;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ActionBarRenderer {

    private final List<RenderableRunnable> renderableRunnables;

    private final List<ResponseHandler<?>> responseHandlers = new ArrayList<>();
    private final UUID formId = UUID.randomUUID();

    public DisplayableElement render() {
        val html = createActionsBar(renderableRunnables);

        return DisplayableElement.builder()
                .id(formId)
                .html(html)
                .responseHandlers(responseHandlers)
                .build();
    }

    private String createActionsBar(final List<RenderableRunnable> runnables) {
        return String.format("""
                        <div>
                            %s
                        </div>
                        """,
                runnables.stream()
                        .map(this::createActionButton)
                        .collect(Collectors.joining())
        );
    }

    private String createActionButton(final RenderableRunnable runnable) {
        val run = Run.builder()
                .runnable(runnable.getRunnable())
                .build();

        val html = String.format("""
                        <button onclick="%s">%s</button>
                        """,
                run.asJsFunction(),
                runnable.getTitle()
        );

        responseHandlers.add(run);

        return html;
    }
}
