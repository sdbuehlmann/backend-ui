package ch.donkeycode.examples.persons.views;

import ch.donkeycode.backendui.html.renderers.model.DisplayableElement;
import ch.donkeycode.backendui.html.utils.CssStyle;
import ch.donkeycode.backendui.html.utils.HtmlElement;
import ch.donkeycode.backendui.navigation.RootViewController;
import ch.donkeycode.backendui.navigation.ViewContext;
import ch.donkeycode.examples.persons.NavigationTargetRegistry;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
public class RootView implements RootViewController {
    @SneakyThrows
    @Override
    public DisplayableElement render(ViewContext context, Void model) {

        Thread.sleep(1000);

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                context.display(NavigationTargetRegistry.MAIN, null);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        return DisplayableElement.builder()
                .html(HtmlElement.builder()
                        .name("h1")
                        .styleAttribute(new CssStyle()
                                .add("color", "white"))
                        .content("ISAFF - Integrierte System Architektur für Frida")
                        .build().toString())
                .build();
    }
}
