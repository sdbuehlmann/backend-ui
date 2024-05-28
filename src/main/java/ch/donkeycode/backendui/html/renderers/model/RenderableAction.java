package ch.donkeycode.backendui.html.renderers.model;

import ch.donkeycode.backendui.html.elements.Icon;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.function.Consumer;

@Value
@Builder
public class RenderableAction<T> {
    @NonNull
    String title;

    @Nullable
    Icon icon;

    @NonNull
    Consumer<T> action;

    public RenderableAction(@NonNull String title, @Nullable Icon icon, @NonNull Consumer<T> action) {
        this.title = title;
        this.icon = icon;
        this.action = action;
    }

    public RenderableAction(@NonNull String title, @NonNull Consumer<T> action) {
        this.title = title;
        this.action = action;
        this.icon = null;
    }
}
