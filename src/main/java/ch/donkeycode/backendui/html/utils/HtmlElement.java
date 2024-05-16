package ch.donkeycode.backendui.html.utils;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Value
@Builder
public class HtmlElement {
    @NonNull
    String name;

    @NonNull
    @Builder.Default
    String content = "";

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
