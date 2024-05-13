package ch.donkeycode.backendui.html.elements.model;

import ch.donkeycode.backendui.frontend.ResponseHandler;
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
    List<ResponseHandler<?>> responseHandlers;
}
