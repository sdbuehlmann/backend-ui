package ch.donkeycode.backendui.html.elements.form.model;

import ch.donkeycode.backendui.html.elements.model.RenderableAction;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder(toBuilder = true)
public class RenderableForm<T> {
    @Singular
    @NonNull
    List<RenderableFormGroup<T>> groups;

    @Singular
    @NonNull
    List<RenderableAction<T>> actions;
}
