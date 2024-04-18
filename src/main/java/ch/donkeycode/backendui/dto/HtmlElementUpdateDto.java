package ch.donkeycode.backendui.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class HtmlElementUpdateDto {
    String elementId;
    String elementHtml;
}
