package ch.donkeycode.backendui.navigation;

import ch.donkeycode.backendui.html.elements.model.DisplayableElement;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class NavigationService {
    private final List<ViewController<?>> viewControllers;

    public ViewContext context(BiConsumer<DisplayableElement, UUID> display, UUID containerId) {
        return ViewContext.builder()
                .containerId(containerId)
                .displayElement(display)
                .viewControllers(viewControllers)
                .build();
    }
}
