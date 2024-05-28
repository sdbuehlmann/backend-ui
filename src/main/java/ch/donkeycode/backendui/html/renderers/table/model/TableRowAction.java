package ch.donkeycode.backendui.html.renderers.table.model;

import ch.donkeycode.backendui.html.elements.Icon;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import lombok.Value;

import java.util.function.Consumer;

@Value
public class TableRowAction<T> {
    @NonNull
    String title;

    @Nullable
    Icon icon;

    @NonNull
    Consumer<T> onAction;

    public TableRowAction(@NonNull String title, @Nullable Icon icon, @NonNull Consumer<T> onAction) {
        this.title = title;
        this.icon = icon;
        this.onAction = onAction;
    }

    public TableRowAction(@NonNull String title, @NonNull Consumer<T> onAction) {
        this.title = title;
        this.onAction = onAction;
        this.icon = null;
    }
}
