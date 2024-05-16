package ch.donkeycode.backendui.html.renderers.table;

import ch.donkeycode.backendui.frontend.ResponseHandler;
import ch.donkeycode.backendui.html.renderers.actionbar.ActionBarRenderer;
import ch.donkeycode.backendui.html.renderers.model.DisplayableElement;
import ch.donkeycode.backendui.html.renderers.model.ReadOnlyStringProperty;
import ch.donkeycode.backendui.html.renderers.model.RenderableRunnable;
import ch.donkeycode.backendui.html.renderers.table.model.RenderableTable;
import ch.donkeycode.backendui.html.renderers.table.model.TableRowAction;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class TableRenderer<T> {

    private final RenderableTable<T> renderableTable;
    private final List<T> data;

    private final List<ResponseHandler<?>> responseHandlers = new ArrayList<>();

    private final UUID formId = UUID.randomUUID();

    public DisplayableElement render() {
        val actionBar = new ActionBarRenderer(renderableTable.getTableActions()).render();

        val html = String.format("""
                        <div style="background: white; padding: 10px;" id="%s">
                            %s
                            %s
                        </table>
                        """,
                formId,
                actionBar.getHtml(),
                createTable());

        return DisplayableElement.builder()
                .html(html)
                .responseHandlers(
                        Stream.concat(actionBar.getResponseHandlers().stream(), responseHandlers.stream())
                                .toList())
                .build();
    }

    private String createTable() {
        return String.format("""
                        <table>
                            %s
                            %s
                        </table>
                        """,
                createHead(),
                createBody());
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
                createHeadEntry("")
        );

        return html;
    }

    private String createHeadEntry(final String title) {
        val html = String.format("""
                        <th style="text-align: left; padding: 8px;">
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
                createActionCell(renderableTable.getRowActions(), data)
        );

        return html;
    }

    private String createCell(ReadOnlyStringProperty<T> property, T data) {
        val html = String.format("""
                        <td style="text-align: left; padding: 8px;">%s</td>
                        """,
                property.getValueExtractor().apply(data)
        );

        return html;
    }

    private String createActionCell(List<TableRowAction<T>> actions, T data) {
        val actionBar = new ActionBarRenderer(actions.stream()
                .map(tableRowAction -> new RenderableRunnable(tableRowAction.getTitle(), () -> tableRowAction.getOnAction().accept(data)))
                .toList()).render();

        val html = String.format("""
                        <td style="text-align: left; padding: 8px;">%s</td>
                        """,
                actionBar.getHtml()
        );

        responseHandlers.addAll(actionBar.getResponseHandlers());

        return html;
    }
}
