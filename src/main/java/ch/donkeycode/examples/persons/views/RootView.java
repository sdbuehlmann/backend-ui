package ch.donkeycode.examples.persons.views;

import ch.donkeycode.backendui.html.colors.ColorSchemeService;
import ch.donkeycode.backendui.DisplayableElement;
import ch.donkeycode.backendui.html.utils.CssStyle;
import ch.donkeycode.backendui.html.utils.HtmlElement;
import ch.donkeycode.backendui.navigation.RootViewController;
import ch.donkeycode.backendui.navigation.ViewContext;
import ch.donkeycode.examples.persons.NavigationTargetRegistry;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RootView implements RootViewController {

    private static final UUID ROOT_ELEMENT_ID = UUID.randomUUID();

    private final ColorSchemeService colorSchemeService;

    @SneakyThrows
    @Override
    public DisplayableElement render(ViewContext context, Void model) {

        Thread.sleep(1000);

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                context
                        .forSubContainer(ROOT_ELEMENT_ID) // TODO: Remove unused div element...
                        .display(NavigationTargetRegistry.MAIN, null);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        val colorScheme = colorSchemeService.getActiveColorScheme();

        val html = HtmlElement.builder()
                .name("div")
                .idAttribute(ROOT_ELEMENT_ID)
                .styleAttribute(new CssStyle()
                        .add("height", "100vh")
                        .add("width", "100%")
                        .add("padding", "0")
                        .add("margin", "0")
                        .add("display", "flex")
                        .add("justify-content", "center")
                        .add("align-items", "center")
                        .backgroundColor(colorScheme.getPrimary()))
                .content(HtmlElement.builder()
                        .name("h1")
                        .styleAttribute(new CssStyle()
                                .add("color", "white"))
                        .content("ISAFF - Integrierte System Architektur für Frida")
                        .build())
                .build();

        return DisplayableElement.builder()
                .html(html.toString())
                .build();
    }
}
