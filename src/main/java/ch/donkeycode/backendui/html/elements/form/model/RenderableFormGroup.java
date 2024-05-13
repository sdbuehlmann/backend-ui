package ch.donkeycode.backendui.html.elements.form.model;

import ch.donkeycode.backendui.html.elements.model.RenderableProperty;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class RenderableFormGroup<T> {
    @NonNull
    String title;

    @Singular
    @NonNull
    List<RenderableProperty<T>> properties;
}
