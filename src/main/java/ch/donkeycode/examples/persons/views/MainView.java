package ch.donkeycode.examples.persons.views;

import ch.donkeycode.backendui.html.elements.model.DisplayableElement;
import ch.donkeycode.backendui.html.elements.model.RenderableRunnable;
import ch.donkeycode.backendui.html.layouts.TabsLayoutRenderer;
import ch.donkeycode.backendui.navigation.NavigationTarget;
import ch.donkeycode.backendui.navigation.ViewContext;
import ch.donkeycode.backendui.navigation.ViewController;
import ch.donkeycode.examples.persons.NavigationTargetRegistry;
import ch.donkeycode.examples.persons.services.PeopleStore;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
@Service
public class MainView implements ViewController<Void> {

    private final PeopleStore peopleStore;

    @Override
    public NavigationTarget<Void> getHandledNavigationTarget() {
        return NavigationTargetRegistry.MAIN;
    }

    @Override
    public DisplayableElement render(ViewContext context, Void model) {
        val containerIdRef = new AtomicReference<UUID>(); // TODO remove dirty hack...
        val subContextRef = new AtomicReference<ViewContext>();

        val tabs = List.of(
                new RenderableRunnable(
                        "Verfügbare WebCams",
                        () -> subContextRef
                                .get()
                                .display(NavigationTargetRegistry.LIST_WEBCAMS, null)
                ),
                new RenderableRunnable(
                        "Esel anzeigen",
                        () -> subContextRef
                                .get()
                                .display(NavigationTargetRegistry.SHOW_DONKEY_IMAGE, null)
                ),
                new RenderableRunnable(
                        "Stammdaten",
                        () -> subContextRef
                                .get()
                                .display(NavigationTargetRegistry.LIST_PEOPLE, peopleStore.getPersons())
                )
        );

        val container = new TabsLayoutRenderer(tabs).render();
        containerIdRef.set(container.getContainerId());
        subContextRef.set(context.forSubContainer(container.getContainerId()));

        return container.getDisplayableElement();
    }
}
