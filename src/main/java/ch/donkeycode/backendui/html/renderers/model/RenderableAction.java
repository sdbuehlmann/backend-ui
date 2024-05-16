package ch.donkeycode.backendui.html.renderers.model;

import lombok.NonNull;
import lombok.Value;

import java.util.function.Consumer;

@Value
public class RenderableAction<T> {
    @NonNull
    String title;

    @NonNull
    Consumer<T> action;
}
