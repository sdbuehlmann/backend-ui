package ch.donkeycode.examples.persons.views;

import ch.donkeycode.backendui.html.elements.ImageRenderer;
import ch.donkeycode.backendui.html.elements.model.DisplayableElement;
import ch.donkeycode.backendui.html.utils.HtmlElement;
import ch.donkeycode.backendui.navigation.NavigationTarget;
import ch.donkeycode.backendui.navigation.ViewContext;
import ch.donkeycode.backendui.navigation.ViewController;
import ch.donkeycode.examples.persons.NavigationTargetRegistry;
import ch.donkeycode.examples.persons.services.WebCamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
@Service
public class WebCamStreamView implements ViewController<WebCamService.CamInfo> {

    private final WebCamService webCamService;

    private final AtomicReference<WebCamService.CapturingHandle> capturingHandleRef = new AtomicReference<>();

    @Override
    public NavigationTarget<WebCamService.CamInfo> getHandledNavigationTarget() {
        return NavigationTargetRegistry.SHOW_WEBCAM_STREAM;
    }

    @Override
    public DisplayableElement render(ViewContext context, WebCamService.CamInfo camInfo) {

        capturingHandleRef.set(webCamService.startCapturing(camInfo, bufferedImage -> {
            context.updateElement(context.getContainerId(), new ImageRenderer(bufferedImage).render());
        }));

        return DisplayableElement.builder()
                .id(UUID.randomUUID())
                .html(HtmlElement.builder()
                        .name("div")
                        .content("Starting webcam stream...")
                        .build().toString())
                .responseHandlers(List.of())
                .build();
    }

    @Override
    public void beforeLeafing(ViewContext context) {
        context.updateElement(context.getContainerId(), DisplayableElement.builder()
                .id(UUID.randomUUID())
                .html(HtmlElement.builder()
                        .name("div")
                        .content("Stop webcam stream...")
                        .build().toString())
                .responseHandlers(List.of())
                .build());

        capturingHandleRef
                .get()
                .stopBlocking();
    }
}
