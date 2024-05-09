package ch.donkeycode.backendui.html.elements.model;

import ch.donkeycode.backendui.form.model.RenderableFormAction;
import ch.donkeycode.backendui.html.elements.model.RenderableAction;
import lombok.Value;

import java.util.UUID;

@Value
public class ActionBinding {
    UUID actionId;
    RenderableAction action;
}
