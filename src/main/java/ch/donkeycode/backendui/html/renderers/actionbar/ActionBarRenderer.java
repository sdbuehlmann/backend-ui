package ch.donkeycode.backendui.html.renderers.actionbar;

import ch.donkeycode.backendui.frontend.functions.JsFunctionGenerator;
import ch.donkeycode.backendui.frontend.functions.Run;
import ch.donkeycode.backendui.html.colors.Color;
import ch.donkeycode.backendui.html.elements.FlatButton;
import ch.donkeycode.backendui.html.elements.Icon;
import ch.donkeycode.backendui.html.renderers.model.RenderableRunnable;
import ch.donkeycode.backendui.html.utils.CssStyle;
import ch.donkeycode.backendui.html.utils.HtmlElement;
import ch.donkeycode.backendui.navigation.ResponseHandlerRegisterer;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Builder
public class ActionBarRenderer {
    @NonNull
    private final List<Action> actions;

    @NonNull
    private final Color backgroundColor;

    @NonNull
    private final Color textColor;

    private final UUID elementId = UUID.randomUUID();

    public HtmlElement render() {
        return HtmlElement.builder()
                .div()
                .idAttribute(elementId)
                .styleAttribute(new CssStyle()
                        .add("padding", "0")
                        .add("gap", "5px")
                        .add("display", "flex")
                        .add("flex-direction", "row"))
                .content(actions.stream()
                        .map(this::createActionButton))
                .build();
    }

    private HtmlElement createActionButton(final Action action) {
        return FlatButton.builder()
                .text(action.getText())
                .onClickFunction(action.getOnClickFunction())
                .backgroundColor(backgroundColor)
                .textColor(textColor)
                .icon(action.getIcon())
                .build()
                .get();
    }

    @Value
    @Builder
    public static class Action {
        JsFunctionGenerator onClickFunction;
        String text;

        @Nullable
        Icon icon;
    }
}
