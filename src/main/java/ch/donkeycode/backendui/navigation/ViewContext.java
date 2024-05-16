package ch.donkeycode.backendui.navigation;

import ch.donkeycode.backendui.html.elements.model.DisplayableElement;
import lombok.Builder;
import lombok.Value;
import lombok.val;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

@Value
@Builder(toBuilder = true)
public class ViewContext {
    UUID containerId;
    BiConsumer<DisplayableElement, UUID> displayElement;
    List<ViewController<?>> viewControllers;

    AtomicReference<Optional<ViewController<?>>> currentViewController = new AtomicReference<>(Optional.empty());

    public <T> void display(NavigationTarget<T> target, T data) {
        val nextViewController = findMatchingViewController(target);

        currentViewController.updateAndGet(previousViewController -> {
            previousViewController.ifPresent(viewController -> viewController.beforeLeafing(this));

            nextViewController.enter(this, data);
            val element =  nextViewController.render(this, data); // TODO Replace trough enter-method ?
            displayElement.accept(element, containerId);

            previousViewController.ifPresent(ViewController::afterLeafing);

            return Optional.of(nextViewController);
        });
    }

    public ViewContext forSubContainer(UUID subContainerId) {
        return new ViewContext(subContainerId, displayElement, viewControllers);
    }

    public void updateElement(UUID containerId, DisplayableElement displayableElement) {
        displayElement.accept(displayableElement, containerId);
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
