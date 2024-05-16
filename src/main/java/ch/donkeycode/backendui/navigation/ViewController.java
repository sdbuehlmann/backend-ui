package ch.donkeycode.backendui.navigation;

import ch.donkeycode.backendui.DisplayableElement;

public interface ViewController<T> {

    NavigationTarget<T> getHandledNavigationTarget();

    DisplayableElement render(ViewContext context, T model);

    default void enter(ViewContext context, T model) {
    }

    default void beforeLeafing(ViewContext context) {
    }

    default void afterLeafing() {
    }
}
