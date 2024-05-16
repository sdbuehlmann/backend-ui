package ch.donkeycode.backendui.html.renderers.table.model;

import ch.donkeycode.backendui.html.renderers.model.ReadOnlyStringProperty;
import ch.donkeycode.backendui.html.renderers.model.RenderableRunnable;
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
