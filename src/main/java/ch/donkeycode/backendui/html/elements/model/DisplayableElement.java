package ch.donkeycode.backendui.html.elements.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
@Builder
public class DisplayableElement {
    UUID id;
    Object data;
    String html;
    List<ElementBinding> bindings;
    List<ActionBinding> actionBindings;
}
