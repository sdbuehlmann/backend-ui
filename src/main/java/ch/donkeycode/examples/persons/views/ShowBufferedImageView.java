package ch.donkeycode.examples.persons.views;

import ch.donkeycode.backendui.html.elements.ImageRenderer;
import ch.donkeycode.backendui.html.elements.model.DisplayableElement;
import ch.donkeycode.backendui.navigation.NavigationTarget;
import ch.donkeycode.backendui.navigation.ViewContext;
import ch.donkeycode.backendui.navigation.ViewController;
import ch.donkeycode.examples.persons.NavigationTargetRegistry;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

@Service
public class ShowBufferedImageView implements ViewController<BufferedImage> {
    @Override
    public NavigationTarget<BufferedImage> getHandledNavigationTarget() {
        return NavigationTargetRegistry.SHOW_BUFFERED_IMAGE;
    }

    @Override
    public DisplayableElement render(ViewContext context, BufferedImage bufferedImage) {
        return new ImageRenderer(bufferedImage).render();
    }
}
