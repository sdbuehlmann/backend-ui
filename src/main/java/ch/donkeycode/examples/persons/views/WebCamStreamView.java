package ch.donkeycode.examples.persons.views;

import ch.donkeycode.backendui.html.elements.HtmlImg;
import ch.donkeycode.backendui.html.layouts.VerticalStackLayout;
import ch.donkeycode.backendui.html.renderers.model.DisplayableElement;
import ch.donkeycode.backendui.html.utils.HtmlElement;
import ch.donkeycode.backendui.navigation.NavigationTarget;
import ch.donkeycode.backendui.navigation.ViewContext;
import ch.donkeycode.backendui.navigation.ViewController;
import ch.donkeycode.examples.persons.NavigationTargetRegistry;
import ch.donkeycode.examples.persons.services.WebCamService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.awt.Dimension;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
@Service
public class WebCamStreamView implements ViewController<WebCamService.CamInfo> {

    private static final int STREAM_WIDTH = 1000;

    private final WebCamService webCamService;

    private final AtomicReference<WebCamService.CapturingHandle> capturingHandleRef = new AtomicReference<>();

    private final UUID imageContainerId = UUID.randomUUID();
    private final UUID imageContainerWrapperId = UUID.randomUUID();

    @Override
    public NavigationTarget<WebCamService.CamInfo> getHandledNavigationTarget() {
        return NavigationTargetRegistry.SHOW_WEBCAM_STREAM;
    }

    @Override
    public DisplayableElement render(ViewContext context, WebCamService.CamInfo camInfo) {
        val targetDimension = scaleToWidth(camInfo.getResolution(), STREAM_WIDTH);

        val camInfoElement = HtmlElement.builder()
                .name("div")
                .content(String.format(
                        "Cam: %s, Resolution: %s/%s",
                        camInfo.getName(),
                        camInfo.getResolution().getWidth(),
                        camInfo.getResolution().getHeight()
                        ))
                .build();

        val imageContainerWrapper = HtmlElement.builder()
                .name("div")
                .idAttribute(imageContainerWrapperId)
                .content(HtmlElement.builder()
                        .name("div")
                        .idAttribute(imageContainerId)
                        .content("Starting webcam stream...")
                        .build())
                .build();

        val layout = VerticalStackLayout.create(
                camInfoElement,
                imageContainerWrapper
        );

        capturingHandleRef.set(webCamService.startCapturing(camInfo, bufferedImage -> {
            val img = HtmlImg.builder()
                    .image(bufferedImage)
                    .dimensionsInPx(targetDimension)
                    .build();

            context.updateElement(imageContainerId, img);
        }));

        return DisplayableElement.builder()
                .html(layout.toString())
                .responseHandlers(List.of())
                .build();
    }

    @Override
    public void beforeLeafing(ViewContext context) {
        capturingHandleRef
                .get()
                .stop();

        context.updateElement(imageContainerWrapperId, DisplayableElement.builder()
                .html(HtmlElement.builder()
                        .name("div")
                        .content("Stop webcam stream...")
                        .build().toString())
                .responseHandlers(List.of())
                .build());

        capturingHandleRef
                .get()
                .waitUntilStopped();
    }

    private static Dimension scaleToWidth(Dimension original, int targetWidth) {
        val heightToWidthRatio = (double) original.height / original.width;
        return new Dimension(targetWidth, (int) (targetWidth * heightToWidthRatio));
    }
}
