package ch.donkeycode.examples.persons.views;

import ch.donkeycode.backendui.html.elements.model.DisplayableElement;
import ch.donkeycode.backendui.html.elements.model.ReadOnlyStringProperty;
import ch.donkeycode.backendui.html.elements.table.TableRenderer;
import ch.donkeycode.backendui.html.elements.table.model.RenderableTable;
import ch.donkeycode.backendui.html.elements.table.model.TableRowAction;
import ch.donkeycode.examples.persons.NavigationTargetRegistry;
import ch.donkeycode.examples.persons.model.Person;
import ch.donkeycode.backendui.navigation.NavigationContext;
import ch.donkeycode.backendui.navigation.NavigationTarget;
import ch.donkeycode.backendui.navigation.ViewController;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListPeople implements ViewController<List<Person>> {
    @Override
    public NavigationTarget<List<Person>> getHandledNavigationTarget() {
        return NavigationTargetRegistry.LIST_PEOPLE;
    }

    @Override
    public DisplayableElement render(NavigationContext navigationContext, List<Person> model) {
        val table = RenderableTable.<Person>builder()
                .property(new ReadOnlyStringProperty<>(
                        "Name",
                        Person::getName
                ))
                .property(new ReadOnlyStringProperty<>(
                        "Vornamen",
                        Person::getPrename
                ))
                .rowAction(new TableRowAction<>(
                        "Bearbeiten",
                        person -> navigationContext
                                .getNavigator()
                                .navigate(NavigationTargetRegistry.EDIT_PERSON, person)
                ))
                .build();


        return new TableRenderer<>(table, model).render();
    }
}
