package ch.donkeycode.examples.persons.views;

import ch.donkeycode.backendui.DisplayableElement;
import ch.donkeycode.backendui.html.colors.ColorSchemeService;
import ch.donkeycode.backendui.html.renderers.model.ReadOnlyStringProperty;
import ch.donkeycode.backendui.html.renderers.table.TableRenderer;
import ch.donkeycode.backendui.html.renderers.table.model.RenderableTable;
import ch.donkeycode.backendui.html.renderers.table.model.TableRowAction;
import ch.donkeycode.backendui.navigation.NavigationTarget;
import ch.donkeycode.backendui.navigation.ViewContext;
import ch.donkeycode.backendui.navigation.ViewController;
import ch.donkeycode.examples.persons.NavigationTargetRegistry;
import ch.donkeycode.examples.persons.services.WebCamService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ListWebCamsView implements ViewController<Void> {

    private final WebCamService webCamService;
    private final ColorSchemeService colorSchemeService; // TODO: In Context? Muss bei Switch eh neu gebaut werden...

    @Override
    public NavigationTarget<Void> getHandledNavigationTarget() {
        return NavigationTargetRegistry.LIST_WEBCAMS;
    }

    @Override
    public DisplayableElement render(ViewContext context, Void model) {
        val cams = webCamService.getAvailableCams();
        val table = RenderableTable.<WebCamService.CamInfo>builder()
                .property(new ReadOnlyStringProperty<>(
                        "Name",
                        WebCamService.CamInfo::getName
                ))
                .property(new ReadOnlyStringProperty<>(
                        "Resolution X/Y",
                        camInfo -> camInfo.getResolution().width + "x" + camInfo.getResolution().height
                ))
                .rowAction(new TableRowAction<>(
                        "Single shot",
                        cam -> context.display(NavigationTargetRegistry.SHOW_BUFFERED_IMAGE, webCamService.takeImage(cam))
                ))
                .rowAction(new TableRowAction<>(
                        "Start stream",
                        cam -> context.display(NavigationTargetRegistry.SHOW_WEBCAM_STREAM, cam)
                ))
                .build();

        return new TableRenderer<>(
                table,
                cams,
                colorSchemeService.getActiveColorScheme(),
                context.getResponseHandlerRegisterer()
        ).render();
    }


}
