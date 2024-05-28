package ch.donkeycode.backendui.common.xml;

import ch.donkeycode.backendui.common.xml.editing.XmlEditor;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
public class XmlParser {

    public XmlEditor parse(String xmlString) {
        val fragments = XmlFragmentsParser.toFragments(xmlString);
        val rootNode = XmlTreeBuilder.build(fragments);

        return new XmlEditor(rootNode);
    }
}
