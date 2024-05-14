package ch.donkeycode.examples.persons.views;

import ch.donkeycode.backendui.html.elements.form.FormRenderer;
import ch.donkeycode.backendui.html.elements.form.model.RenderableForm;
import ch.donkeycode.backendui.html.elements.form.model.RenderableFormGroup;
import ch.donkeycode.backendui.html.elements.model.DisplayableElement;
import ch.donkeycode.backendui.html.elements.model.ReadOnlyStringProperty;
import ch.donkeycode.backendui.html.elements.model.ReadWriteStringProperty;
import ch.donkeycode.backendui.html.elements.model.RenderableAction;
import ch.donkeycode.examples.persons.Converter;
import ch.donkeycode.examples.persons.NavigationTargetRegistry;
import ch.donkeycode.examples.persons.model.Buildable;
import ch.donkeycode.examples.persons.model.Person;
import ch.donkeycode.backendui.navigation.NavigationContext;
import ch.donkeycode.backendui.navigation.NavigationTarget;
import ch.donkeycode.backendui.navigation.ViewController;
import ch.donkeycode.examples.persons.services.PeopleStore;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EditPerson implements ViewController<Person> {

    private final PeopleStore peopleStore;

    @Override
    public NavigationTarget<Person> getHandledNavigationTarget() {
        return NavigationTargetRegistry.EDIT_PERSON;
    }

    @Override
    public DisplayableElement render(NavigationContext context, Person model) {
        val form = RenderableForm.<Person>builder()
                .builderCreator(Buildable::toBuilder)
                .group(RenderableFormGroup.<Person>builder()
                        .title("Technische Daten")
                        .property(new ReadOnlyStringProperty<>(
                                "ID",
                                Converter.objToString(Person::getId)
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                "Zuletzt geändert",
                                Converter.localDateTimeToString(Person::getLastUpdatedAt)
                        ))
                        .build())
                .group(RenderableFormGroup.<Person>builder()
                        .title("Persönliche Daten")
                        .property(new ReadWriteStringProperty<>(
                                "Name",
                                Person::getName,
                                Person.PersonBuilder::name
                        ))
                        .property(new ReadWriteStringProperty<>(
                                "Vornamen",
                                Person::getPrename,
                                Person.PersonBuilder::prename
                        ))
                        .build())
                .action(new RenderableAction<>(
                        "Speichern",
                        person -> {
                            peopleStore.update(person.getId(), unused -> person.toBuilder()
                                        .lastUpdatedAt(LocalDateTime.now())
                                        .build());
                            context
                                    .getNavigator()
                                    .navigate(
                                            NavigationTargetRegistry.LIST_PEOPLE,
                                            peopleStore.getPersons());
                        }
                ))
                .action(new RenderableAction<>(
                        "Abbrechen",
                        person -> context
                                .getNavigator()
                                .navigate(
                                        NavigationTargetRegistry.LIST_PEOPLE,
                                        peopleStore.getPersons())
                ))
                .build();

        return new FormRenderer<>(form, model).render();
    }
}
