package ch.donkeycode.backendui;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class DisplayableElement {
    @NonNull
    String html;

    @NonNull
    @Builder.Default
    List<ResponseHandler<?>> responseHandlers = List.of();
}
