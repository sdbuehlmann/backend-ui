package ch.donkeycode.backendui.html.renderers.model;

import ch.donkeycode.backendui.html.elements.Icon;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import lombok.Value;

@Value
public class RenderableRunnable {
    @NonNull
    String title;

    @Nullable
    Icon icon;

    @NonNull
    Runnable runnable;

    public RenderableRunnable(@NonNull String title, @Nullable Icon icon, @NonNull Runnable runnable) {
        this.title = title;
        this.icon = icon;
        this.runnable = runnable;
    }

    public RenderableRunnable(@NonNull String title, @NonNull Runnable runnable) {
        this.title = title;
        this.runnable = runnable;
        this.icon = null;
    }
}
