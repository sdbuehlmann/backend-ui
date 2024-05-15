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
}
