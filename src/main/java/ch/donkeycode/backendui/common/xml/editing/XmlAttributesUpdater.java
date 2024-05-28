package ch.donkeycode.backendui.common.xml.editing;

import ch.donkeycode.backendui.common.xml.model.XmlAttribute;
import ch.donkeycode.backendui.common.xml.model.XmlNode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class XmlAttributesUpdater {

    public static UnaryOperator<XmlNode.XmlNodeBuilder> setOrReplaceAttributesUpdater(XmlAttribute... attributes) {
        val attributeNames = Arrays.stream(attributes)
                .map(XmlAttribute::getName)
                .collect(Collectors.toSet());

        return xmlNodeBuilder -> {
            val oldAttributes = xmlNodeBuilder.build().getAttributes();

            val existingAttributes = oldAttributes.stream()
                    .filter(xmlAttribute -> attributeNames.contains(xmlAttribute.getName()))
                    .toList();

            val updatableAttributes = new HashSet<>(oldAttributes);
            updatableAttributes.removeAll(existingAttributes);

            updatableAttributes.addAll(Set.of(attributes));

            return xmlNodeBuilder
                    .clearAttributes()
                    .attributes(updatableAttributes);
        };
    }

    public static Predicate<XmlNode> hasAttributeSelector(String attributeName) {
        return xmlNode -> xmlNode.getAttributes().stream()
                .anyMatch(xmlAttribute -> xmlAttribute.getName().equals(attributeName));
    }
}
