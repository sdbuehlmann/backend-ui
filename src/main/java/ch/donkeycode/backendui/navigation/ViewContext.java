package ch.donkeycode.backendui.navigation;

import ch.donkeycode.backendui.html.elements.model.DisplayableElement;
import lombok.Builder;
import lombok.Value;
import lombok.val;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

@Value
@Builder(toBuilder = true)
public class ViewContext {
    UUID containerId;
    BiConsumer<DisplayableElement, UUID> displayElement;
    List<ViewController<?>> viewControllers;

    public <T> void display(NavigationTarget<T> target, T data) {
        val viewController = findMatchingViewController(target);
        val element = viewController.render(this, data);
        displayElement.accept(element, containerId);
    }

    public ViewContext forSubContainer(UUID subContainerId) {
        return new ViewContext(subContainerId, displayElement, viewControllers);
    }

    private <T> ViewController<T> findMatchingViewController(NavigationTarget<T> target) {
        return viewControllers.stream()
                .filter(controller -> controller.getHandledNavigationTarget().equals(target))
                .map(controller -> (ViewController<T>) controller)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("No view controller found for target " + target));
    }

        /*
        update...
        delete...
        ...
         */
}
