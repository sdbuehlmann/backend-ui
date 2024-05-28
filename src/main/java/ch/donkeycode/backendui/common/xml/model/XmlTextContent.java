package ch.donkeycode.backendui.common.xml.model;

import lombok.Value;

import java.util.List;

@Value
public class XmlTextContent implements XmlElement {

    String text;

    @Override
    public List<XmlElement> getChilds() {
        return List.of();
    }

    @Override
    public String toString() {
        return text;
    }
}
