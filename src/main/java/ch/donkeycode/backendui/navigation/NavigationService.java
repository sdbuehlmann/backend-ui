package ch.donkeycode.backendui.navigation;

import ch.donkeycode.backendui.html.elements.model.DisplayableElement;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Service
//@RequiredArgsConstructor
public class NavigationService {
    private final List<ViewController<?>> viewControllers;

    public NavigationService(List<ViewController<?>> viewControllers) {
        this.viewControllers = viewControllers;
    }

    public NavigationContext context(Consumer<DisplayableElement> display) {
        final Navigator navigator = new Navigator() {
            @Override
            public <T> void navigate(NavigationTarget<T> target, T data) {
                val viewController = findMatchingViewController(target);
                val element = viewController.render(new NavigationContext(this), data);
                display.accept(element);
            }
        };

        return new NavigationContext(navigator);
    }

    private <T> ViewController<T> findMatchingViewController(NavigationTarget<T> target) {
        return viewControllers.stream()
                .filter(controller -> controller.getHandledNavigationTarget().equals(target))
                .map(controller -> (ViewController<T>) controller)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("No view controller found for target " + target));
    }
}
