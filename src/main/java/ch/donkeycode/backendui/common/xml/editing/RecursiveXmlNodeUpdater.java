package ch.donkeycode.backendui.common.xml.editing;

import ch.donkeycode.backendui.common.xml.model.XmlNode;
import lombok.Value;
import lombok.val;

import java.util.function.Predicate;
import java.util.function.UnaryOperator;

@Value
public class RecursiveXmlNodeUpdater {
    XmlNode rootNode;
    Predicate<XmlNode> selector;
    UnaryOperator<XmlNode.XmlNodeBuilder> updater;

    public XmlNode update() {
        return updateIfNeeded(rootNode);
    }

    private XmlNode updateIfNeeded(XmlNode xmlNode) {
        val builder = xmlNode.toBuilder();

        builder.clearChildElements()
                .childElements(xmlNode.getChildElements().stream()
                        .flatMap(xmlElement -> xmlElement.asNode().stream())
                        .map(this::updateIfNeeded)
                        .toList());

        if (selector.test(xmlNode)) {
            return updater.apply(builder).build();
        }

        return builder.build();
    }
}
