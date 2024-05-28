package ch.donkeycode.backendui.common.xml.editing;

import ch.donkeycode.backendui.common.xml.model.XmlNode;
import lombok.Value;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

@Value
public class XmlEditor {

    private final AtomicReference<XmlNode> rootNodeRef;

    public XmlEditor(XmlNode rootNode) {
        this.rootNodeRef = new AtomicReference<>(rootNode);
    }

    public XmlEditor updateNodes(Predicate<XmlNode> selector, UnaryOperator<XmlNode.XmlNodeBuilder> updater) {
        rootNodeRef.updateAndGet(rootNode -> new RecursiveXmlNodeUpdater(rootNode, selector, updater).update());
        return this;
    }

    public String toHtmlString() {
        return rootNodeRef.get().toString();
    }
}