package ch.donkeycode.examples.persons.views;

import ch.donkeycode.backendui.html.elements.model.DisplayableElement;
import ch.donkeycode.backendui.html.elements.model.ReadOnlyStringProperty;
import ch.donkeycode.backendui.html.elements.table.TableRenderer;
import ch.donkeycode.backendui.html.elements.table.model.RenderableTable;
import ch.donkeycode.backendui.html.elements.table.model.TableRowAction;
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
                        "Select",
                        cam -> context.display(NavigationTargetRegistry.SHOW_BUFFERED_IMAGE, webCamService.takeImage(cam))
                ))
                .build();

        return new TableRenderer<>(table, cams).render();
    }


}
