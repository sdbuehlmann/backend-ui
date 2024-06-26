package ch.donkeycode.examples.persons.views;

import ch.donkeycode.backendui.DisplayableElement;
import ch.donkeycode.backendui.html.colors.ColorSchemeService;
import ch.donkeycode.backendui.html.renderers.form.FormRenderer;
import ch.donkeycode.backendui.html.renderers.form.model.RenderableForm;
import ch.donkeycode.backendui.html.renderers.form.model.RenderableFormGroup;
import ch.donkeycode.backendui.html.renderers.model.ReadOnlyStringProperty;
import ch.donkeycode.backendui.html.renderers.model.ReadWriteStringProperty;
import ch.donkeycode.backendui.html.renderers.model.RenderableAction;
import ch.donkeycode.backendui.navigation.NavigationTarget;
import ch.donkeycode.backendui.navigation.ViewContext;
import ch.donkeycode.backendui.navigation.ViewController;
import ch.donkeycode.examples.persons.Converter;
import ch.donkeycode.examples.persons.NavigationTargetRegistry;
import ch.donkeycode.examples.persons.model.Buildable;
import ch.donkeycode.examples.persons.model.Person;
import ch.donkeycode.examples.persons.services.PeopleStore;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EditPerson implements ViewController<Person> {

    private final PeopleStore peopleStore;
    private final ColorSchemeService colorSchemeService;

    @Override
    public NavigationTarget<Person> getHandledNavigationTarget() {
        return NavigationTargetRegistry.EDIT_PERSON;
    }

    @Override
    public DisplayableElement render(ViewContext context, Person model) {
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
                            peopleStore.createOrUpdate(person.getId(), unused -> person.toBuilder()
                                    .lastUpdatedAt(LocalDateTime.now())
                                    .build());
                            context
                                    .display(
                                            NavigationTargetRegistry.LIST_PEOPLE,
                                            peopleStore.getPersons());
                        }
                ))
                .action(new RenderableAction<>(
                        "Abbrechen",
                        person -> context
                                .display(
                                        NavigationTargetRegistry.LIST_PEOPLE,
                                        peopleStore.getPersons())
                ))
                .build();

        return new FormRenderer<>(
                form,
                model,
                context.getResponseHandlerRegisterer(),
                colorSchemeService.getActiveColorScheme()
        ).render();
    }
}
