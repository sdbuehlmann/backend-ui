package ch.donkeycode.backendui.common.xml.model;

import ch.donkeycode.utils.CastHelper;

import java.util.List;
import java.util.Optional;

public interface XmlElement {
    List<XmlElement> getChilds();

    default Optional<XmlNode> asNode() {
        return CastHelper.tryCast(this, XmlNode.class);
    }

    default Optional<XmlTextContent> asTextContent() {
        return CastHelper.tryCast(this, XmlTextContent.class);
    }
}
