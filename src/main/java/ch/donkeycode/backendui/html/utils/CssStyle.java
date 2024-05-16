package ch.donkeycode.backendui.html.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CssStyle {
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
