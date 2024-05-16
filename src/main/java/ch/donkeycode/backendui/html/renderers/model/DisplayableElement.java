package ch.donkeycode.backendui.html.renderers.model;

import ch.donkeycode.backendui.frontend.ResponseHandler;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
@Builder
public class DisplayableElement {
    @NonNull
    String html;

    @NonNull
    @Builder.Default
    List<ResponseHandler<?>> responseHandlers = List.of();
}
