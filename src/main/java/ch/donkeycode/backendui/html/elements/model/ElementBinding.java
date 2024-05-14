package ch.donkeycode.backendui.html.elements.model;

import lombok.Value;

import java.util.UUID;

@Value
public class ElementBinding {
    UUID elementId;
    ReadWriteStringProperty<?,?> property;
}
