package ch.donkeycode.backendui.frontend.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@Builder
@Jacksonized
public class HtmlElementUpdateDto {
    UUID elementId;
    String elementHtml;

    @Override
    public String toString() {
        return "HtmlElementUpdateDto{" +
               "elementId=" + elementId +
               ", elementHtml='" + getCropped(elementHtml, 10) + '\'' +
               '}';
    }

    private String getCropped(String orig, int maxLength) {
        if (orig.length() <= maxLength) {
            return orig;
        }

        return orig.substring(0, maxLength) + "...";
    }
}
