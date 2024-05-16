package ch.donkeycode.backendui.navigation;

import ch.donkeycode.backendui.DisplayableElement;
import ch.donkeycode.backendui.html.utils.HtmlElement;
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
    RootViewController rootViewController;

    AtomicReference<Optional<ViewController<?>>> currentViewController = new AtomicReference<>(Optional.empty());

    public <T> void display(NavigationTarget<T> target, T data) {
        val nextViewController = findMatchingViewController(target);
        display(nextViewController, data);
    }

    public void displayRoot() {
        display(rootViewController, null);
    }

    public ViewContext forSubContainer(UUID subContainerId) {
        return new ViewContext(subContainerId, displayElement, viewControllers, rootViewController);
    }

    public void updateElement(UUID containerId, DisplayableElement displayableElement) {
        displayElement.accept(displayableElement, containerId);
    }

    public void updateElement(UUID containerId, HtmlElement element) {
        displayElement.accept(DisplayableElement.builder()
                        .html(element.toString())
                        .build(),
                containerId);
    }

    public void update(HtmlElement element) {
        displayElement.accept(DisplayableElement.builder()
                        .html(element.toString())
                        .build(),
                containerId);
    }

    public <T> void display(ViewController<T> nextViewController, T data) {
        currentViewController.updateAndGet(previousViewController -> {
            previousViewController.ifPresent(viewController -> viewController.beforeLeafing(this));

            nextViewController.enter(this, data);
            val element = nextViewController.render(this, data); // TODO Replace trough enter-method ?
            displayElement.accept(element, containerId);

            previousViewController.ifPresent(ViewController::afterLeafing);

            return Optional.of(nextViewController);
        });
    }

    private <T> ViewController<T> findMatchingViewController(NavigationTarget<T> target) {
        return viewControllers.stream()
                .filter(controller -> controller.getHandledNavigationTarget().equals(target))
                .map(controller -> (ViewController<T>) controller)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("No view controller found for target " + target));
    }
}
