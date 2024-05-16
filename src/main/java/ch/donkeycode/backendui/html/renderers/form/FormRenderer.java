package ch.donkeycode.backendui.html.renderers.form;

import ch.donkeycode.backendui.frontend.ResponseHandler;
import ch.donkeycode.backendui.frontend.functions.CollectValuesAndRun;
import ch.donkeycode.backendui.html.renderers.form.model.RenderableForm;
import ch.donkeycode.backendui.html.renderers.form.model.RenderableFormGroup;
import ch.donkeycode.backendui.html.renderers.model.DisplayableElement;
import ch.donkeycode.backendui.html.renderers.model.ReadOnlyStringProperty;
import ch.donkeycode.backendui.html.renderers.model.ReadWriteStringProperty;
import ch.donkeycode.backendui.html.renderers.model.RenderableAction;
import ch.donkeycode.examples.persons.model.Buildable;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FormRenderer<T extends Buildable<T>> {

    private final RenderableForm<T> form;
    private final T data;
    private final Buildable.Builder<T> builder;

    private final List<CollectValuesAndRun.CollectableElement> collectableElements = new ArrayList<>();
    private final List<ResponseHandler<?>> responseHandlers = new ArrayList<>();

    private final UUID formId = UUID.randomUUID();

    public FormRenderer(RenderableForm<T> form, T data) {
        this.form = form;
        this.data = data;
        this.builder = data.toBuilder();
    }

    public DisplayableElement render() {
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
                createActionsBar(form.getActions()));

        return DisplayableElement.builder()
                .id(formId)
                .html(html)
                .responseHandlers(responseHandlers)
                .build();
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
                                return createField((ReadWriteStringProperty<T, ?>) renderableProperty, data);
                            }

                            if (renderableProperty instanceof ReadOnlyStringProperty) {
                                return createField((ReadOnlyStringProperty<T>) renderableProperty, data);
                            }

                            throw new IllegalArgumentException(String.format(
                                    "Property type %s is not supported yet.",
                                    renderableProperty.getClass().getName()
                            ));
                        })
                        .collect(Collectors.joining()));

        return html;
    }

    private String createField(ReadOnlyStringProperty<T> property, T data) {
        val html = String.format("""
                        <div style="display: flex; flex-direction: column; margin: 10px;">
                        	<label>%s</label>
                          	<input type="text" value="%s" disabled>
                        </div>
                        """,
                property.getTitle(),
                property.getValueExtractor().apply(data)
        );

        return html;
    }


    private String createField(final ReadWriteStringProperty<T, ?> property, final T data) {
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

        collectableElements.add(
                new CollectValuesAndRun.CollectableElement(
                        elementId,
                        value -> property
                                .getValuePersistor()
                                .persistUnchecked(builder, value)));

        return html;
    }

    private String createActionsBar(final List<RenderableAction<T>> actions) {
        return String.format("""
                        <div>
                            %s
                        </div>
                        """,
                actions.stream()
                        .map(this::createActionButton)
                        .collect(Collectors.joining())
        );
    }

    private String createActionButton(final RenderableAction<T> action) {
        val valuesCollector = CollectValuesAndRun.builder()
                .parentElementId(formId)
                .collectableElements(collectableElements)
                .runnable(() -> action
                        .getAction()
                        .accept(builder.build()))
                .build();

        val html = String.format("""
                        <button onclick="%s">%s</button>
                        """,
                valuesCollector.asJsFunction(),
                action.getTitle()
        );

        responseHandlers.add(valuesCollector);

        return html;
    }
}