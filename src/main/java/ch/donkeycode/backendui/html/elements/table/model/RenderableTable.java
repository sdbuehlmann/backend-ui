package ch.donkeycode.backendui.html.elements.table.model;

import ch.donkeycode.backendui.html.elements.model.ReadOnlyStringProperty;
import ch.donkeycode.backendui.html.elements.model.RenderableAction;
import ch.donkeycode.backendui.html.elements.model.RenderableRunnable;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class RenderableTable<T> {

    @Singular
    @NonNull
    List<ReadOnlyStringProperty<T>> properties;

    @Singular
    @NonNull
    List<TableRowAction<T>> rowActions;

    @Singular
    @NonNull
    List<RenderableRunnable> tableActions;
}
