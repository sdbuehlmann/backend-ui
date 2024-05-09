package ch.donkeycode.backendui.html.elements.model;

import lombok.NonNull;
import lombok.Value;

import java.util.function.Function;

@Value
public class ReadOnlyStringProperty<T> implements RenderableProperty<T> {
    @NonNull String title;
    @NonNull Function<T, String> valueExtractor;

    public ReadOnlyStringProperty(
            @NonNull String title,
            @NonNull Function<T, String> valueExtractor
    ) {
        this.title = title;
        this.valueExtractor = valueExtractor;
    }
}
