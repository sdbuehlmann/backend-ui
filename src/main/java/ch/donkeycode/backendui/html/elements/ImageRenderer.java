package ch.donkeycode.backendui.html.elements;

import ch.donkeycode.backendui.html.elements.model.DisplayableElement;
import ch.donkeycode.backendui.html.utils.HtmlElement;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class ImageRenderer {

    private static final Base64.Encoder ENCODER = Base64.getEncoder();

    private final BufferedImage image;

    private final UUID elementId = UUID.randomUUID();

    public DisplayableElement render() {

        val base64EncodedImage = encodeBase64(image);

        val html = HtmlElement.builder()
                .name("img")
                .attribute("id", elementId.toString())
                .attribute("src", "data:image/png;base64," + base64EncodedImage)
                .build();

        return DisplayableElement.builder()
                .id(elementId)
                .html(html.toString())
                .responseHandlers(List.of())
                .build();
    }

    @SneakyThrows
    private String encodeBase64(BufferedImage image) {
        val outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        return ENCODER.encodeToString(outputStream.toByteArray());
    }
}
