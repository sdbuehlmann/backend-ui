package ch.donkeycode.backendui.form.examples;

import ch.donkeycode.backendui.html.elements.model.ReadWriteStringProperty;
import ch.donkeycode.backendui.form.model.RenderableForm;
import ch.donkeycode.backendui.form.model.RenderableFormAction;
import ch.donkeycode.backendui.form.model.RenderableFormGroup;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExampleForm {

    public static final RenderableForm<ExampleForm> FORM = RenderableForm.<ExampleForm>builder()
            .group(RenderableFormGroup.<ExampleForm>builder()
                    .title("Persönliche Daten")
                    .property(new ReadWriteStringProperty<>(
                            "Name",
                            ExampleForm::getName,
                            ExampleForm::setName
                    ))
                    .property(new ReadWriteStringProperty<>(
                            "Vornamen",
                            ExampleForm::getPrename,
                            ExampleForm::setPrename
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


      String name;
      String prename;
}
