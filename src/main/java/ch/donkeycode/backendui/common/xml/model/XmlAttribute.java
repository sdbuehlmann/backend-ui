package ch.donkeycode.backendui.common.xml.model;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.Optional;

@Value
public class XmlAttribute {
    String name;

    @EqualsAndHashCode.Exclude
    Optional<String> value;

    public XmlAttribute(String name) {
        this.name = name;
        this.value = Optional.empty();
    }

    public XmlAttribute(String name, String value) {
        this.name = name;
        this.value = Optional.of(value);
    }

    @Override
    public String toString() {
        return value.map(s -> String.format("%s=\"%s\"", name, s))
                .orElseGet(() -> name);
    }
}
