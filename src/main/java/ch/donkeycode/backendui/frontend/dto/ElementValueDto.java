package ch.donkeycode.backendui.frontend.dto;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@Builder
@Jacksonized
public class ElementValueDto {
    @NonNull UUID elementId;
    @NonNull String value;
}
