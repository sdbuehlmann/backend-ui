package ch.donkeycode.examples.persons.views;

import ch.donkeycode.backendui.html.elements.form.FormRenderer;
import ch.donkeycode.backendui.html.elements.form.model.RenderableForm;
import ch.donkeycode.backendui.html.elements.form.model.RenderableFormGroup;
import ch.donkeycode.backendui.html.elements.model.DisplayableElement;
import ch.donkeycode.backendui.html.elements.model.ReadWriteStringProperty;
import ch.donkeycode.backendui.html.elements.model.RenderableAction;
import ch.donkeycode.examples.persons.NavigationTargetRegistry;
import ch.donkeycode.examples.persons.model.Person;
import ch.donkeycode.backendui.navigation.NavigationContext;
import ch.donkeycode.backendui.navigation.NavigationTarget;
import ch.donkeycode.backendui.navigation.ViewController;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EditPerson implements ViewController<Person> {
    @Override
    public NavigationTarget<Person> getHandledNavigationTarget() {
        return NavigationTargetRegistry.EDIT_PERSON;
    }

    @Override
    public DisplayableElement render(NavigationContext context, Person model) {
        val form = RenderableForm.<Person>builder()
                .group(RenderableFormGroup.<Person>builder()
                        .title("Persönliche Daten")
                        .property(new ReadWriteStringProperty<>(
                                "Name",
                                Person::getName,
                                Person::setName
                        ))
                        .property(new ReadWriteStringProperty<>(
                                "Vornamen",
                                Person::getPrename,
                                Person::setPrename
                        ))
                        .build())
                .action(new RenderableAction<>(
                        "Speichern",
                        person -> {
                            System.out.println("TODO: Speichern");
                            context
                                    .getNavigator()
                                    .navigate(
                                            NavigationTargetRegistry.LIST_PEOPLE,
                                            List.of(
                                                    Person.builder().name("Hans").prename("Muster").build(),
                                                    Person.builder().name("Peter").prename("Müller").build(),
                                                    Person.builder().name("Anna").prename("Meier").build()
                                            ));
                        }
                ))
                .action(new RenderableAction<>(
                        "Abbrechen",
                        person -> System.out.println("TODO: Abbrechen")
                ))
                .build();

        return new FormRenderer<>(form, model).render();
    }
}
