package ch.donkeycode.backendui.html.utils;

import ch.donkeycode.backendui.html.colors.Color;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CssStyle {
    private final List<KeyValue> entries = new ArrayList<>();

    public CssStyle add(String key, String value) {
        entries.add(new KeyValue(key, value));
        return this;
    }

    public CssStyle backgroundColor(Color color) {
        return add("background-color", color.getCssColor());
    }

    public CssStyle dimensionInPx(Dimension dimension) {
        add("width", dimension.width + "px");
        add("height", dimension.height + "px");

        return this;
    }

    public CssStyle color(Color color) {
        return add("color", color.getCssColor());
    }

    public String toInlineStyle() {
        return entries.stream()
                .map(cssStyleEntry -> String.format("%s: %s;", cssStyleEntry.getKey(), cssStyleEntry.getValue()))
                .collect(Collectors.joining());

    }
}
