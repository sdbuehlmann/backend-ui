package ch.donkeycode.backendui.html.elements.table;

import ch.donkeycode.backendui.html.elements.model.ActionBinding;
import ch.donkeycode.backendui.html.elements.model.DisplayableElement;
import ch.donkeycode.backendui.html.elements.model.ElementBinding;
import ch.donkeycode.backendui.html.elements.model.ReadOnlyStringProperty;
import ch.donkeycode.backendui.html.elements.table.model.RenderableTable;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TableRenderer<T> {

    private final RenderableTable<T> renderableTable;
    private final List<T> data;

    private final List<ElementBinding> elementBindings = new ArrayList<>();
    private final List<ActionBinding> actionBindings = new ArrayList<>();

    private final UUID formId = UUID.randomUUID();

    public DisplayableElement render() {
        val html = String.format("""
                        <table style="background: white; padding: 10px;" id="%s">
                            %s
                            %s
                        </table>
                        """,
                formId,
                createHead(),
                createBody());

        return DisplayableElement.builder()
                .id(formId)
                .data(data)
                .html(html)
                .bindings(elementBindings)
                .actionBindings(actionBindings)
                .build();
    }

    private String createHead() {
        val html = String.format("""
                        <thead>
                            <tr>
                                %s
                            </tr>
                        </thead>
                        """,
                renderableTable.getProperties().stream()
                        .map(this::createHeadEntry)
                        .collect(Collectors.joining())
        );

        return html;
    }

    private String createHeadEntry(final ReadOnlyStringProperty<T> property) {
        val html = String.format("""
                        <th>
                            %s
                        </th>
                        """,
                property.getTitle());

        return html;
    }

    private String createBody() {
        val html = String.format("""
                        <tbody>
                            %s
                        </tbody>
                        """,
                data.stream()
                        .map(this::createRow)
                        .collect(Collectors.joining())
        );

        return html;
    }

    private String createRow(T data) {
        val html = String.format("""
                        <tr>
                            %s
                        </tr>
                        """,
                renderableTable.getProperties().stream()
                        .map(property -> createCell(property, data))
                        .collect(Collectors.joining())
        );

        return html;
    }

    private String createCell(ReadOnlyStringProperty<T> property, T data) {
        val html = String.format("""
                        <td>%s</td>
                        """,
                property.getValueExtractor().apply(data)
        );

        return html;
    }
}
