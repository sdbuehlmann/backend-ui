package ch.donkeycode.backendui.html.examples.persons;

import ch.donkeycode.backendui.form.model.RenderableForm;
import ch.donkeycode.backendui.form.model.RenderableFormAction;
import ch.donkeycode.backendui.form.model.RenderableFormGroup;
import ch.donkeycode.backendui.html.elements.model.ReadOnlyStringProperty;
import ch.donkeycode.backendui.html.elements.model.ReadWriteStringProperty;
import ch.donkeycode.backendui.html.elements.table.model.RenderableTable;

public class RendererInstructions {
    public static final RenderableTable<Person> TABLE = RenderableTable.<Person>builder()
            .property(new ReadOnlyStringProperty<>(
                    "Name",
                    Person::getName
            ))
            .property(new ReadOnlyStringProperty<>(
                    "Vornamen",
                    Person::getPrename
            ))
            .build();

    public static final RenderableForm<Person> FORM = RenderableForm.<Person>builder()
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
            .action(new RenderableFormAction(
                    "Speichern",
                    () -> System.out.println("TODO: Speichern")
            ))
            .action(new RenderableFormAction(
                    "Zurücksetzen",
                    () -> System.out.println("TODO: Zurücksetzen")
            ))
            .build();
}
