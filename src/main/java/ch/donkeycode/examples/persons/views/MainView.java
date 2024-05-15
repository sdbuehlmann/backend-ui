package ch.donkeycode.examples.persons.views;

import ch.donkeycode.backendui.html.elements.model.DisplayableElement;
import ch.donkeycode.backendui.html.elements.model.RenderableRunnable;
import ch.donkeycode.backendui.html.layouts.TabsLayoutRenderer;
import ch.donkeycode.backendui.navigation.ViewContext;
import ch.donkeycode.backendui.navigation.NavigationTarget;
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
        val tabs = List.of(
                new RenderableRunnable(
                        "Settings",
                        () -> System.out.println("Display settings")
                ),
                new RenderableRunnable(
                        "Monitoring",
                        () -> System.out.println("Display monitoring")
                ),
                new RenderableRunnable(
                        "Stammdaten",
                        () -> {
                            context
                                    .forSubContainer(containerIdRef.get())
                                    .display(NavigationTargetRegistry.LIST_PEOPLE, peopleStore.getPersons());
                        }
                )
        );

        val container = new TabsLayoutRenderer(tabs).render();
        containerIdRef.set(container.getContainerId());

        return container.getDisplayableElement();
    }
}
