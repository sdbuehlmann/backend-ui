package ch.donkeycode.backendui.form.model;

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
    List<RenderableFormAction> actions;

    public interface AfterSaveChangesAction<T> {
        void doAfterSaveChanges(T edited) throws Exception;
    }
}
