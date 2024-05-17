package ch.donkeycode.backendui.html.renderers.table;

import ch.donkeycode.backendui.DisplayableElement;
import ch.donkeycode.backendui.frontend.functions.Run;
import ch.donkeycode.backendui.html.colors.ColorScheme;
import ch.donkeycode.backendui.html.renderers.actionbar.ActionBarRenderer;
import ch.donkeycode.backendui.html.renderers.model.ReadOnlyStringProperty;
import ch.donkeycode.backendui.html.renderers.table.model.RenderableTable;
import ch.donkeycode.backendui.html.renderers.table.model.TableRowAction;
import ch.donkeycode.backendui.navigation.ResponseHandlerRegisterer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TableRenderer<T> {

    private final RenderableTable<T> renderableTable;
    private final List<T> data;

    @NonNull
    private final ColorScheme colorScheme;

    @NonNull
    private final ResponseHandlerRegisterer responseHandlerRegisterer;

    private final UUID elementId = UUID.randomUUID();

    public DisplayableElement render() {
        val barActions = renderableTable.getTableActions().stream()
                .map(renderableRunnable -> {
                    val onClickFunction = responseHandlerRegisterer.registerAndReturn(Run.builder()
                            .relatedElementId(elementId)
                            .runnable(renderableRunnable.getRunnable())
                            .build());
                    return ActionBarRenderer.Action.builder()
                            .onClickFunction(onClickFunction)
                            .text(renderableRunnable.getTitle())
                            .build();
                })
                .collect(Collectors.toList());


        val actionBar = ActionBarRenderer.builder()
                .backgroundColor(colorScheme.getDarker())
                .actions(barActions)
                .build().render();

        val html = String.format("""
                        <div id="%s">
                            %s
                            %s
                        </table>
                        """,
                elementId,
                actionBar,
                createTable());

        return DisplayableElement.builder()
                .html(html)
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
        val barActions = actions.stream()
                .map(tableRowAction -> {
                    val onClickFunction = responseHandlerRegisterer.registerAndReturn(Run.builder()
                            .relatedElementId(elementId)
                            .runnable(() -> tableRowAction.getOnAction().accept(data))
                            .build());
                    return ActionBarRenderer.Action.builder()
                            .onClickFunction(onClickFunction)
                            .text(tableRowAction.getTitle())
                            .build();
                })
                .collect(Collectors.toList());


        val actionBar = ActionBarRenderer.builder()
                .backgroundColor(colorScheme.getDarker())
                .actions(barActions)
                .build().render();

        return String.format("""
                        <td style="text-align: left; padding: 8px;">%s</td>
                        """,
                actionBar.toString()
        );
    }
}
