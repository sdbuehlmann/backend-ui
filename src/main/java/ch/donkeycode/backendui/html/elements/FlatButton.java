package ch.donkeycode.backendui.html.elements;

import ch.donkeycode.backendui.frontend.functions.JsFunctionGenerator;
import ch.donkeycode.backendui.html.colors.Color;
import ch.donkeycode.backendui.html.utils.CssStyle;
import ch.donkeycode.backendui.html.utils.HtmlElement;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.UUID;

@RequiredArgsConstructor
@Builder
public class FlatButton {

    @NonNull
    private final String text;
    @NonNull
    private final JsFunctionGenerator onClickFunction;
    @NonNull
    private final Color backgroundColor;
//    @NonNull
//    private final Dimension dimensionsInPx;

    private final UUID elementId = UUID.randomUUID();

    public HtmlElement get() {
        val style = new CssStyle()
//                .dimensionInPx(dimensionsInPx)
                .add("margin", "0")
                .add("padding", "10px")
                .backgroundColor(backgroundColor)
                .add("cursor", "pointer");

        return HtmlElement.builder()
                .name("div")
                .idAttribute(elementId)
                .styleAttribute(style)
                .attribute("onClick", onClickFunction.asJsFunction())
                .content(text)
                .build();
    }
}
