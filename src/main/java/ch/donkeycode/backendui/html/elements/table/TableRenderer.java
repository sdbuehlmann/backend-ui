package ch.donkeycode.backendui.html.elements.table;

import ch.donkeycode.backendui.frontend.ResponseHandler;
import ch.donkeycode.backendui.frontend.functions.Run;
import ch.donkeycode.backendui.html.elements.model.ActionBinding;
import ch.donkeycode.backendui.html.elements.model.DisplayableElement;
import ch.donkeycode.backendui.html.elements.model.ElementBinding;
import ch.donkeycode.backendui.html.elements.model.ReadOnlyStringProperty;
import ch.donkeycode.backendui.html.elements.table.model.RenderableTable;
import ch.donkeycode.backendui.html.elements.table.model.TableRowAction;
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

    private final List<ResponseHandler<?>> responseHandlers = new ArrayList<>();

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
                .responseHandlers(responseHandlers)
                .build();
    }

    private String createHead() {
        val html = String.format("""
                        <thead>
                            <tr>
                                %s
                                %s
                            </tr>
                        </thead>
                        """,
                renderableTable.getProperties().stream()
                        .map(property -> createHeadEntry(property.getTitle()))
                        .collect(Collectors.joining()),
                renderableTable.getRowActions().stream()
                        .map(action -> createHeadEntry("Actions"))
                        .collect(Collectors.joining())
        );

        return html;
    }

    private String createHeadEntry(final String title) {
        val html = String.format("""
                        <th>
                            %s
                        </th>
                        """,
                title);

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
                            %s
                        </tr>
                        """,
                renderableTable.getProperties().stream()
                        .map(property -> createCell(property, data))
                        .collect(Collectors.joining()),
                renderableTable.getRowActions().stream()
                        .map(action -> createActionCell(action, data))
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

    private String createActionCell(TableRowAction<T> action, T data) {
        val function = Run.builder()
                .runnable(() -> action.getOnAction().accept(data))
                .build();

        val html = String.format("""
                        <td><a onclick="%s">%s</a></td>
                        """,
                function.asJsFunction(),
                action.getTitle()
        );

        responseHandlers.add(function);

        return html;
    }
}
