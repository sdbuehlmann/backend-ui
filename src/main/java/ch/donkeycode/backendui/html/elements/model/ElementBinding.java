package ch.donkeycode.backendui.html.elements.model;

import ch.donkeycode.backendui.html.elements.model.ReadWriteStringProperty;
import lombok.Value;

import java.util.UUID;

@Value
public class ElementBinding {
    UUID elementId;
    ReadWriteStringProperty<?> property;
}
