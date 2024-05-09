package ch.donkeycode.backendui.html.elements.model;

import lombok.NonNull;
import lombok.Value;

@Value
public class RenderableAction {
    @NonNull
    String title;

    @NonNull
    Runnable onAction;
}
