package ch.donkeycode.backendui.html.layouts;

import ch.donkeycode.backendui.html.utils.CssStyle;
import ch.donkeycode.backendui.html.utils.HtmlElement;
import lombok.val;

public class VerticalStackLayout {
    public static HtmlElement create(HtmlElement... elements) {
        val style = new CssStyle()
                .add("width", "100%")
                .add("height", "100%")
                .add("padding", "10px")
                .add("display", "flex")
                .add("flex-direction", "column");

        return HtmlElement.builder()
                .name("div")
                .styleAttribute(style)
                .content(elements)
                .build();
    }
}
