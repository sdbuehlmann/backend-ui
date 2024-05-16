package ch.donkeycode.examples.persons.views;

import ch.donkeycode.backendui.common.ResourcesResolver;
import ch.donkeycode.backendui.html.elements.ImageRenderer;
import ch.donkeycode.backendui.html.elements.model.DisplayableElement;
import ch.donkeycode.backendui.navigation.NavigationTarget;
import ch.donkeycode.backendui.navigation.ViewContext;
import ch.donkeycode.backendui.navigation.ViewController;
import ch.donkeycode.examples.persons.NavigationTargetRegistry;
import lombok.val;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;

@Service
public class ShowDonkeyImageView implements ViewController<Void> {
    @Override
    public NavigationTarget<Void> getHandledNavigationTarget() {
        return NavigationTargetRegistry.SHOW_DONKEY_IMAGE;
    }

    @Override
    public DisplayableElement render(ViewContext context, Void model) {
        try(val inputStream = ResourcesResolver.loadResource("images/donkey.png")) {
            val image = ImageIO.read(inputStream);

            return new ImageRenderer(image).render();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load image", e);
        }
    }
}
