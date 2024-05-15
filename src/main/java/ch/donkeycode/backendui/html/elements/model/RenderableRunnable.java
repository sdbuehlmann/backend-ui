package ch.donkeycode.backendui.html.elements.model;

import lombok.NonNull;
import lombok.Value;

@Value
public class RenderableRunnable {
    @NonNull
    String title;

    @NonNull
    Runnable runnable;
}
