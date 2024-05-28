package ch.donkeycode.backendui.html.elements;

import ch.donkeycode.backendui.common.ResourcesResolver;
import ch.donkeycode.backendui.common.xml.XmlParser;
import ch.donkeycode.backendui.common.xml.editing.XmlAttributesUpdater;
import ch.donkeycode.backendui.common.xml.model.XmlAttribute;
import ch.donkeycode.backendui.html.colors.Color;
import lombok.val;

import java.awt.Dimension;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public enum Icon {
    VIDEO_CAMERA("icons/video-icon.svg"),
    DOCUMENT_WITH_PLUS("icons/document_with_plus.svg"),
    TRASH("icons/trash.svg"),
    PENCIL("icons/pencil.svg");

    private static final XmlParser XML_PARSER = new XmlParser();

    private final String path;

    Icon(String path) {
        this.path = path;
    }

    public String load(Color color, Dimension dimensionInPx) {
        try (val inputStream = ResourcesResolver.loadResource(path)) {
            val svg = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            return XML_PARSER
                    .parse(svg)
                    .updateNodes(
                            xmlNode -> xmlNode.getName().equals("svg"),
                            XmlAttributesUpdater.setOrReplaceAttributesUpdater(
                                    new XmlAttribute("height", dimensionInPx.height + "px"),
                                    new XmlAttribute("width", dimensionInPx.width + "px")))
                    .updateNodes(
                            XmlAttributesUpdater.hasAttributeSelector("stroke"),
                            XmlAttributesUpdater.setOrReplaceAttributesUpdater(
                                    new XmlAttribute("stroke", color.getHexColor())))
                    .toHtmlString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
