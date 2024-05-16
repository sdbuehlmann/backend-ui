package ch.donkeycode.backendui.navigation;

import ch.donkeycode.backendui.DisplayableElement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

@Service
@RequiredArgsConstructor
public class NavigationService {
    private final RootViewController rootViewController;
    private final List<ViewController<?>> viewControllers;

    public ViewContext context(BiConsumer<DisplayableElement, UUID> display, UUID containerId) {
        return ViewContext.builder()
                .containerId(containerId)
                .displayElement(display)
                .rootViewController(rootViewController)
                .viewControllers(viewControllers)
                .build();
    }
}
