package ch.donkeycode.backendui.html.elements;

import ch.donkeycode.backendui.frontend.functions.JsFunctionGenerator;
import ch.donkeycode.backendui.html.colors.Color;
import ch.donkeycode.backendui.html.utils.CssStyle;
import ch.donkeycode.backendui.html.utils.HtmlElement;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.lang.Nullable;

import java.awt.Dimension;
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

    @NonNull
    private final Color textColor;

//    @NonNull
//    private final Dimension dimensionsInPx;

    @Nullable
    private final Icon icon;

    private final UUID elementId = UUID.randomUUID();

    public HtmlElement get() {
        val style = new CssStyle()
                .add("margin", "0")
                .add("padding", "10px")
                .backgroundColor(backgroundColor)
                .add("cursor", "pointer");

        if (icon != null) {
            return HtmlElement.builder()
                    .name("div")
                    .idAttribute(elementId)
                    .styleAttribute(style
                            .add("display", "flex")
                            .add("gap", "5px")
                            .add("flex-direction", "row")
                            .add("align-items", "center"))
                    .attribute("onClick", onClickFunction.asJsFunction())
                    .content(
                            icon.load(textColor, new Dimension(20, 20)),
                            HtmlElement.builder()
                                    .div()
                                    .content(text)
                                    .build().toString())
                    .build();
        }

        return HtmlElement.builder()
                .name("div")
                .idAttribute(elementId)
                .styleAttribute(style)
                .attribute("onClick", onClickFunction.asJsFunction())
                .content(text)
                .build();
    }
}
