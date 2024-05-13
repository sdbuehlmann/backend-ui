package ch.donkeycode.backendui.html.elements.model;

import ch.donkeycode.backendui.navigation.NavigationContext;
import lombok.Value;

import java.util.UUID;
import java.util.function.Consumer;

@Value
public class ActionBinding {
    UUID actionId;
    Consumer<NavigationContext> action;
}
