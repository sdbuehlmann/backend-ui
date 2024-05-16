package ch.donkeycode.backendui.html.elements;

import ch.donkeycode.backendui.html.utils.CssStyle;
import ch.donkeycode.backendui.html.utils.HtmlElement;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Builder
@RequiredArgsConstructor
public class HtmlImg {

    private static final Base64.Encoder ENCODER = Base64.getEncoder();

    private final BufferedImage image;
    private final Dimension dimensionsInPx;

    public static class HtmlImgBuilder {
        public HtmlElement build() {
            val base64EncodedImage = encodeBase64(image);

            val css = new CssStyle()
                    .add("width", dimensionsInPx.width + "px")
                    .add("height", dimensionsInPx.height + "px");

            val html = HtmlElement.builder()
                    .name("img")
                    .attribute("src", "data:image/png;base64," + base64EncodedImage)
                    .styleAttribute(css)
                    .build();

            return html;
        }

        @SneakyThrows
        private String encodeBase64(BufferedImage image) {
            val outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);
            return ENCODER.encodeToString(outputStream.toByteArray());
        }
    }
}