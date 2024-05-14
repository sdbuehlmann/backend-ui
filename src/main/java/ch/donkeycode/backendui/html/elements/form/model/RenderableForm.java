package ch.donkeycode.backendui.html.elements.form.model;

import ch.donkeycode.backendui.html.elements.model.RenderableAction;
import ch.donkeycode.examples.persons.model.Buildable;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

import java.util.List;
import java.util.function.Function;

@Value
@Builder(toBuilder = true)
public class RenderableForm<T> {
    @Singular
    @NonNull
    List<RenderableFormGroup<T>> groups;

    @Singular
    @NonNull
    List<RenderableAction<T>> actions;

    @NonNull
    Function<Buildable<T>, Buildable.Builder<T>> builderCreator;
}
