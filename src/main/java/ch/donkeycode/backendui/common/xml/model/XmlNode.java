package ch.donkeycode.backendui.common.xml.model;

import ch.donkeycode.backendui.common.xml.XmlFragmentsParser;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Value
@Builder(toBuilder = true)
public class XmlNode implements XmlElement {

    @NonNull
    String name;

    @NonNull
    @Singular
    List<XmlElement> childElements;

    @NonNull
    @Singular
    Set<XmlAttribute> attributes;

    @Override
    public List<XmlElement> getChilds() {
        return null;
    }

    @Override
    public String toString() {
        if (childElements.isEmpty()) {
            return String.format("<%s %s/>",
                    name,
                    attributes.stream()
                    .map(XmlAttribute::toString)
                    .collect(Collectors.joining(" ")));
        }
        return String.format(" <%s %s>%s</%s>",
                name,
                attributes.stream()
                        .map(XmlAttribute::toString)
                        .collect(Collectors.joining(" ")),
                childElements.stream()
                        .map(XmlElement::toString)
                        .collect(Collectors.joining()),
                name);
    }
}
