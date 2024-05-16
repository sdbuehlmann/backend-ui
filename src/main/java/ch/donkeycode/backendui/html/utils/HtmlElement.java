package ch.donkeycode.backendui.html.utils;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Value
@Builder
public class HtmlElement {
    @NonNull
    String name;

    @NonNull
    @Builder.Default
    String content = "";

    @NonNull
    @Builder.Default
    Set<KeyValue> attributes = Set.of();

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
            if (this.attributes$value == null) {
                this.attributes$value = new HashSet<>();
                this.attributes$set = true;
            }

            this.attributes$value.add(new KeyValue(key, value));
            return this;
        }

        public HtmlElementBuilder idAttribute(UUID id) {
            this.attribute("id", id.toString());
            return this;
        }

        public HtmlElementBuilder styleAttribute(CssStyle style) {
            this.attribute("style", style.toInlineStyle());
            return this;
        }

        public HtmlElementBuilder styleAttribute(Optional<CssStyle> optional) {
            optional.ifPresent(this::styleAttribute);
            return this;
        }

        public HtmlElementBuilder content(String... content) {
            this.content$value = String.join("", content);
            this.content$set = true;

            return this;
        }

        public HtmlElementBuilder content(List<HtmlElement> elements) {
            return content(elements.stream());
        }

        public HtmlElementBuilder content(Stream<HtmlElement> stream) {
            this.content$value = stream
                    .map(HtmlElement::toString)
                    .collect(Collectors.joining());
            this.content$set = true;

            return this;
        }

        public HtmlElementBuilder content(HtmlElement... elements) {
            return content(Arrays.stream(elements));
        }
    }
}
