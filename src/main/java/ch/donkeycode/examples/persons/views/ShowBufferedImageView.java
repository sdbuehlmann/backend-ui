package ch.donkeycode.examples.persons.views;

import ch.donkeycode.backendui.html.elements.HtmlImg;
import ch.donkeycode.backendui.html.renderers.model.DisplayableElement;
import ch.donkeycode.backendui.navigation.NavigationTarget;
import ch.donkeycode.backendui.navigation.ViewContext;
import ch.donkeycode.backendui.navigation.ViewController;
import ch.donkeycode.examples.persons.NavigationTargetRegistry;
import com.github.sarxos.webcam.WebcamResolution;
import lombok.val;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.UUID;

@Service
public class ShowBufferedImageView implements ViewController<BufferedImage> {
    @Override
    public NavigationTarget<BufferedImage> getHandledNavigationTarget() {
        return NavigationTargetRegistry.SHOW_BUFFERED_IMAGE;
    }

    @Override
    public DisplayableElement render(ViewContext context, BufferedImage bufferedImage) {
        val img = HtmlImg.builder()
                .image(bufferedImage)
                .dimensionsInPx(WebcamResolution.HD.getSize())
                .build();

        return DisplayableElement.builder()
                .id(UUID.randomUUID()) // TODO: ?
                .html(img.toString())
                .build();
    }
}
