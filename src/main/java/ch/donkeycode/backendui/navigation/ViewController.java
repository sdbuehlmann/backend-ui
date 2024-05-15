package ch.donkeycode.backendui.navigation;

import ch.donkeycode.backendui.html.elements.model.DisplayableElement;

public interface ViewController<T> {

    NavigationTarget<T> getHandledNavigationTarget();

    DisplayableElement render(ViewContext context, T model);
}
