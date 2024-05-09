package ch.donkeycode.backendui.form.model;

import lombok.NonNull;
import lombok.Value;

@Value
public class RenderableFormAction {
    @NonNull
    String title;

    @NonNull
    Runnable onAction;
}
