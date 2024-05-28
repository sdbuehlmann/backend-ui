package ch.donkeycode.examples.persons.views;

import ch.donkeycode.backendui.DisplayableElement;
import ch.donkeycode.backendui.html.colors.ColorSchemeService;
import ch.donkeycode.backendui.html.elements.Icon;
import ch.donkeycode.backendui.html.renderers.model.ReadOnlyStringProperty;
import ch.donkeycode.backendui.html.renderers.model.RenderableRunnable;
import ch.donkeycode.backendui.html.renderers.table.TableRenderer;
import ch.donkeycode.backendui.html.renderers.table.model.RenderableTable;
import ch.donkeycode.backendui.html.renderers.table.model.TableRowAction;
import ch.donkeycode.backendui.navigation.ViewContext;
import ch.donkeycode.backendui.navigation.NavigationTarget;
import ch.donkeycode.backendui.navigation.ViewController;
import ch.donkeycode.examples.persons.Converter;
import ch.donkeycode.examples.persons.NavigationTargetRegistry;
import ch.donkeycode.examples.persons.model.Person;
import ch.donkeycode.examples.persons.services.PeopleStore;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ListPeople implements ViewController<List<Person>> {

    private final PeopleStore peopleStore;
    private final ColorSchemeService colorSchemeService;

    @Override
    public NavigationTarget<List<Person>> getHandledNavigationTarget() {
        return NavigationTargetRegistry.LIST_PEOPLE;
    }

    @Override
    public DisplayableElement render(ViewContext viewContext, List<Person> model) {
        val table = RenderableTable.<Person>builder()
                .tableAction(new RenderableRunnable(
                        "Neu",
                        Icon.DOCUMENT_WITH_PLUS,
                        () -> viewContext.display(NavigationTargetRegistry.EDIT_PERSON, Person.builder()
                                        .prename("")
                                        .name("")
                                        .build())
                ))
                .property(new ReadOnlyStringProperty<>(
                        "ID",
                        Converter.objToString(Person::getId)
                ))
                .property(new ReadOnlyStringProperty<>(
                        "Zuletzt geändert",
                        Converter.localDateTimeToString(Person::getLastUpdatedAt)
                ))
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
                        Icon.PENCIL,
                        person -> viewContext.display(NavigationTargetRegistry.EDIT_PERSON, person)
                ))
                .rowAction(new TableRowAction<>(
                        "Löschen",
                        Icon.TRASH,
                        person -> {
                            peopleStore.deleteById(person.getId());
                            viewContext.display(NavigationTargetRegistry.LIST_PEOPLE, peopleStore.getPersons());
                        }
                ))
                .build();


        return new TableRenderer<>(
                table,
                model,
                colorSchemeService.getActiveColorScheme(),
                viewContext.getResponseHandlerRegisterer()
        ).render();
    }
}
