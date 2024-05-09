package ch.donkeycode.backendui.form;

import ch.donkeycode.backendui.form.model.ReadWriteStringProperty;
import ch.donkeycode.backendui.form.model.RenderableForm;
import ch.donkeycode.backendui.form.model.RenderableFormAction;
import ch.donkeycode.backendui.form.model.RenderableFormGroup;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class FormRenderer<T> {

    private final RenderableForm<T> form;
    private final T data;

    private final List<ElementBinding<T>> bindings = new ArrayList<>();
    private final List<ActionBinding> actionBindings = new ArrayList<>();

    private final UUID formId = UUID.randomUUID();

    public RendererdForm<T> render() {
        val html = String.format("""
                        <div style="background: white; padding: 10px;" id="%s">
                            %s
                            %s
                        </div>
                        """,
                formId,
                form.getGroups().stream()
                        .map(group -> createGroup(group, data))
                        .collect(Collectors.joining()),
                render(form.getActions()));

        return new RendererdForm<>(formId, data, html, bindings, actionBindings);
    }

    private String createGroup(final RenderableFormGroup<T> group, final T data) {
        val html = String.format("""
                        <div>
                            <label>%s</label>
                            <div>
                                %s
                            </div>
                        </div>
                        """,
                group.getTitle(),
                group.getProperties().stream()
                        .map(renderableProperty -> {
                            if (renderableProperty instanceof ReadWriteStringProperty) {
                                return createField((ReadWriteStringProperty<T>) renderableProperty, data);
                            }

                            throw new IllegalArgumentException(String.format(
                                    "Property type %s ins not supported yet.",
                                    renderableProperty.getClass().getName()
                            ));
                        })
                        .collect(Collectors.joining()));

        return html;
    }


    private String createField(final ReadWriteStringProperty<T> property, final T data) {
        val elementId = UUID.randomUUID();
        val html = String.format("""
                        <div style="display: flex; flex-direction: column; margin: 10px;">
                        	<label>%s</label>
                          	<input type="text" id="%s" value="%s">
                        </div>
                        """,
                property.getTitle(),
                elementId,
                property.getValueExtractor().apply(data)
        );

        bindings.add(new ElementBinding<>(elementId, property));

        return html;
    }

    private String render(final List<RenderableFormAction> actions) {
        return String.format("""
                        <div>
                            %s
                        </div>
                        """,
                actions.stream()
                        .map(this::render)
                        .collect(Collectors.joining())
        );
    }

    private String render(final RenderableFormAction action) {
        val actionId = UUID.randomUUID();
        val html = String.format("""
                        <button onclick="collectAllValues('%s','%s')">%s</button>
                        """,
                actionId,
                formId,
                action.getTitle()
        );

        actionBindings.add(new ActionBinding(actionId, action));

        return html;
    }

    @Value
    public static class ElementBinding<T> {
        UUID elementId;
        ReadWriteStringProperty<T> property;
    }

    @Value
    public static class ActionBinding {
        UUID actionId;
        RenderableFormAction action;
    }

    @Value
    public static class RendererdForm<T> {
        UUID id;
        T data;
        String html;
        List<ElementBinding<T>> bindings;
        List<ActionBinding> actionBindings;
    }
}
