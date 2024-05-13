package ch.donkeycode.backendui.html.elements.table.model;

import lombok.NonNull;
import lombok.Value;

import java.util.function.Consumer;

@Value
public class TableRowAction<T> {
    @NonNull
    String title;

    @NonNull
    Consumer<T> onAction;
}
