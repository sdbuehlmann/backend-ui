package ch.donkeycode.backendui.html.renderers.form;

import ch.donkeycode.backendui.DisplayableElement;
import ch.donkeycode.backendui.frontend.functions.CollectValuesAndRun;
import ch.donkeycode.backendui.html.colors.ColorScheme;
import ch.donkeycode.backendui.html.renderers.actionbar.ActionBarRenderer;
import ch.donkeycode.backendui.html.renderers.form.model.RenderableForm;
import ch.donkeycode.backendui.html.renderers.form.model.RenderableFormGroup;
import ch.donkeycode.backendui.html.renderers.model.ReadOnlyStringProperty;
import ch.donkeycode.backendui.html.renderers.model.ReadWriteStringProperty;
import ch.donkeycode.backendui.html.renderers.model.RenderableAction;
import ch.donkeycode.backendui.html.utils.HtmlElement;
import ch.donkeycode.backendui.navigation.ResponseHandlerRegisterer;
import ch.donkeycode.examples.persons.model.Buildable;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class FormRenderer<T extends Buildable<T>> {

    private final RenderableForm<T> form;
    private final T data;
    private Buildable.Builder<T> builder;

    private final List<CollectValuesAndRun.CollectableElement> collectableElements = new ArrayList<>();

    private final UUID formId = UUID.randomUUID();

    private final ResponseHandlerRegisterer responseHandlerRegisterer;
    private final ColorScheme colorScheme;

    public DisplayableElement render() {
        this.builder = data.toBuilder();

        val html = String.format("""
                        <div id="%s">
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
                .html(html)
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

    private HtmlElement createActionsBar(final List<RenderableAction<T>> actions) {
        return ActionBarRenderer.builder()
                .actions(actions.stream()
                        .map(this::createAction)
                        .toList())
                .backgroundColor(colorScheme.getDarker())
                .textColor(colorScheme.getText())
                .build().render();
    }

    private ActionBarRenderer.Action createAction(final RenderableAction<T> action) {
        val valuesCollector = responseHandlerRegisterer.registerAndReturn(CollectValuesAndRun.builder()
                .parentElementId(formId)
                .collectableElements(collectableElements)
                .runnable(() -> action
                        .getAction()
                        .accept(builder.build()))
                .build());

        return ActionBarRenderer.Action.builder()
                .onClickFunction(valuesCollector)
                .text(action.getTitle())
                .build();
    }
}
